package com.lankaspot.server.service.payment;

import com.lankaspot.server.service.AuditLogService;
import com.lankaspot.server.service.DiscountCodeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.lankaspot.server.enums.*;
import com.lankaspot.server.exception.*;
import com.lankaspot.server.model.*;
import com.lankaspot.server.repository.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayhereNotifyService {

    // Ledger account codes (must be seeded, see below)
    private static final String ACC_CLEARING = "PAYHERE_CLEARING";
    private static final String ACC_DISCOUNT = "DISCOUNT_GIVEN";
    private static final String ACC_UNEARNED = "UNEARNED_AD_REVENUE";
    private static final String REF_PAYMENT_REGISTRY = "PAYMENT_REGISTRY";

    private final PayhereHashService hashService;
    private final ObjectMapper objectMapper;
    private final PaymentRegistryRepository registryRepository;
    private final AdQuoteRepository adQuoteRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final AdvertisementTierRepository tierRepository;
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementTierAssignmentRepository tierAssignmentRepository;
    private final AdPolicyRepository adPolicyRepository;
    private final EntityMappingRepository entityMappingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final LedgerAccountRepository ledgerAccountRepository;
    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final UserRepository userRepository;
    private final DiscountCodeService discountCodeService;
    private final AuditLogService auditLogService;
    private final PayHerePaymentNotificationRepository notificationRepository;

    private static final Pattern FORMAT = Pattern.compile("^LS-(\\d+)-([A-HJ-NP-Z2-9]{8})$");

    /*
     * Controller call order:
     *   notifyService.insertNotification(params);   // tx 1
     *   notifyService.handleLedger(params);         // tx 2
     *   notifyService.handleFulfilment(params);     // tx 3
     */

    /* ------------------------- tx 2: registry + ledger + audit ------------------------- */

    @Transactional
    public void handleLedger(Map<String, String> p) {
        String orderId = p.get("order_id");
        String amountStr = p.get("payhere_amount");
        String currency = p.get("payhere_currency");
        String statusCode = p.get("status_code");
        String md5sig = p.get("md5sig");

        if (orderId == null || amountStr == null || currency == null || statusCode == null || md5sig == null) {
            throw new InvalidPayhereNotificationException("Missing required fields");
        }

        // 1. Signature (constant-time compare)
        String expected = hashService.notifyHash(orderId, amountStr, currency, statusCode);
        if (!MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                md5sig.toUpperCase().getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidPayhereNotificationException("Bad md5sig for order " + orderId);
        }

        // 2. Lock the registry row: PayHere can deliver duplicates concurrently
        PaymentRegistry reg = registryRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new InvalidPayhereNotificationException("Unknown order " + orderId));

        int status = Integer.parseInt(statusCode.trim());
        LocalDateTime now = LocalDateTime.now();

        // 3. Amount / currency must match what we signed at checkout
        if (new BigDecimal(amountStr).compareTo(reg.getFinalAmount()) != 0
                || !reg.getCurrency().equals(currency)) {
            log.error("PayHere amount mismatch for {}: expected {} {}, got {} {}",
                    orderId, reg.getFinalAmount(), reg.getCurrency(), amountStr, currency);
            if (reg.getStatus() == PaymentRegistryStatus.PENDING) {
                reg.setStatus(PaymentRegistryStatus.FAILED);
                reg.setStatusMessage("Amount/currency mismatch");
            }
            return; // 200 so PayHere stops retrying; needs manual review
        }

        // 4. Record gateway data
        reg.setPayherePaymentId(p.get("payment_id"));
        reg.setPayhereStatusCode(status);
        reg.setStatusMessage(truncate(p.get("status_message"), 500));
        reg.setPayhereMethod(p.get("method"));
        reg.setNotifiedAt(now);
        User payer = userRepository.findByKeycloakUserId(reg.getPayerId().toString())
                .orElseThrow(() -> new IllegalStateException("Payer not found: " + reg.getPayerId()));

        switch (status) {
            case 2 -> {
                if (reg.getStatus() == PaymentRegistryStatus.PENDING) {
                    reg.setStatus(PaymentRegistryStatus.SUCCESS);
                    reg.setPaidAt(now);
                }
                // Idempotency: never post the ledger twice for the same order
                if (reg.getStatus() == PaymentRegistryStatus.SUCCESS
                        && !ledgerTransactionRepository.existsByReferenceTypeAndReferenceId(REF_PAYMENT_REGISTRY, reg.getId())) {
                    postLedger(reg, now);
                    auditLogService.record(payer, "PAYMENT_RECEIVED", REF_PAYMENT_REGISTRY, reg.getId(),
                            null,
                            Map.of("orderId", reg.getOrderId(),
                                    "subtotal", reg.getSubtotal(),
                                    "discountAmount", reg.getDiscountAmount(),
                                    "finalAmount", reg.getFinalAmount(),
                                    "currency", reg.getCurrency()));
                } else {
                    log.info("Duplicate success notify, ledger already posted: {}", orderId);
                }
            }
            case 0 -> log.info("Payment pending: {}", orderId);
            case -1 -> markIfPending(reg, PaymentRegistryStatus.CANCELED);
            case -2 -> markIfPending(reg, PaymentRegistryStatus.FAILED);
            case -3 -> {
                if (reg.getStatus() == PaymentRegistryStatus.SUCCESS) {
                    reg.setStatus(PaymentRegistryStatus.CHARGED_BACK);

                    auditLogService.record(payer, "PAYMENT_CHARGEBACK",
                            "PAYMENT_REGISTRY", reg.getId(), "Chargeback received for order " + orderId, null);
                    log.error("CHARGEBACK for {}: needs manual action", orderId);
                }
            }
            default -> log.warn("Unhandled PayHere status {} for {}", status, orderId);
        }
    }

    /* ------------------------- tx 3: everything else ------------------------- */

    @Transactional
    public void handleFulfilment(Map<String, String> p) {
        String orderId = p.get("order_id");
        LocalDateTime now = LocalDateTime.now();

        PaymentRegistry reg = registryRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalStateException("Unknown order " + orderId));

        // Only fulfil paid orders, and only once
        if (reg.getStatus() != PaymentRegistryStatus.SUCCESS || reg.getPayment() != null) {
            log.info("Skipping fulfilment for {}: status={}, alreadyFulfilled={}",
                    orderId, reg.getStatus(), reg.getPayment() != null);
            return;
        }

        AdQuote quote = adQuoteRepository.findByIdForUpdate(reg.getQuoteRef())
                .orElseThrow(() -> new IllegalStateException("Quote gone: " + reg.getQuoteRef()));
        User payer = userRepository.findByKeycloakUserId(reg.getPayerId().toString())
                .orElseThrow(() -> new IllegalStateException("Payer not found: " + reg.getPayerId()));
        SubscriptionPlan plan = subscriptionPlanRepository.findById(reg.getPlanId())
                .orElseThrow(() -> new IllegalStateException("Plan gone: " + reg.getPlanId()));

        // Tiers come from the checkout SNAPSHOT, not the live quote (the quote stays editable until paid)
        List<AdvertisementTier> tiers = tiersFromSnapshot(reg.getLineItemsSnapshot());
        LocalDateTime adExpiry = expiryFor(plan.getBillingPeriod(), now);

        // 1. Advertisement + tier assignments
        Advertisement ad = new Advertisement();
        ad.setAdvertiser(payer);
        ad.setCategory(quote.getCategory());
        ad.setTitle(quote.getTitle());
        ad.setSlug(buildSlug(quote.getTitle(), ad.getAdvertisementRef()));
        ad.setDescription(quote.getDescription());
        ad.setPrice(quote.getPrice());
        ad.setDistrict(quote.getCity().getDistrict().getName());
        ad.setCity(quote.getCity().getName());
        ad.setArea(quote.getTown());
        ad.setAddress(quote.getAddress());
        ad.setStatus(AdvertisementStatus.PENDING_REVIEW);
        ad.setExpiresAt(adExpiry);
        advertisementRepository.save(ad);

        for (AdvertisementTier t : tiers) {
            AdvertisementTierAssignment a = new AdvertisementTierAssignment();
            a.setAdvertisement(ad);
            a.setTier(t);
            a.setStartsAt(now);
            a.setExpiresAt(now.plusDays(t.getDurationDays()));
            tierAssignmentRepository.save(a);
        }

        // 2. AdPolicy (one per quote; tier = most expensive add-on, else default null)
        AdvertisementTier policyTier = tiers.stream()
                .max(Comparator.comparing(AdvertisementTier::getPrice))
                .orElse(null);
        AdPolicy policy = new AdPolicy(reg.getSubtotal(), reg.getCurrency(), reg.getDiscountAmount(), policyTier);
        policy.setSource(AdSource.USER);
        policy.setStatus(AdPolicyStatus.PENDING_REVIEW);
        policy.setAdvertisement(ad);
        adPolicyRepository.save(policy);

        // 3. Quote -> Policy mapping, and close the quote
        entityMappingRepository.save(new EntityMapping(quote, policy));
        quote.setStatus(AdQuoteStatus.PENDING_REVIEW);

        // 4. Payment + gateway transaction
        Payment payment = new Payment();
        payment.setAdvertisement(ad);
        payment.setPolicy(policy);
        payment.setPayer(payer);
        payment.setAmount(reg.getSubtotal());
        payment.setDiscountAmount(reg.getDiscountAmount());
        payment.setFinalAmount(reg.getFinalAmount());
        payment.setCurrency(reg.getCurrency());
        payment.setPaymentMethod(PaymentMethod.PAYHERE);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setExternalReference(reg.getOrderId());
        payment.setProviderReference(p.get("payment_id"));
        payment.setPaidAt(now);
        payment.setCreatedAt(now);
        paymentRepository.save(payment);

        PaymentTransaction ptx = new PaymentTransaction();
        ptx.setPayment(payment);
        ptx.setTransactionReference(p.get("payment_id"));
        ptx.setAmount(reg.getFinalAmount());
        ptx.setTransactionStatus("SUCCESS");
        ptx.setResponseCode(p.get("status_code"));
        ptx.setResponseMessage(truncate(p.get("status_message"), 500));
        ptx.setCreatedAt(now);
        paymentTransactionRepository.save(ptx);

        reg.setPayment(payment);

        // Consume the discount code now that payment is confirmed
        if (reg.getDiscountCodeId() != null) {
            discountCodeService.consume(reg.getDiscountCodeId(), reg.getPayerId().toString(), payment);
        }

        // 5. Link the ledger transaction (posted in handleLedger) to the policy
        ledgerTransactionRepository.findByReferenceTypeAndReferenceId(REF_PAYMENT_REGISTRY, reg.getId())
                .ifPresent(t -> t.setPolicy(policy));

        // 6. Audit
        auditLogService.record(payer, "PAYMENT_SUCCESS", "PAYMENT", payment.getId(),
                null,
                Map.of("orderId", reg.getOrderId(),
                        "policyId", policy.getId(),
                        "subtotal", reg.getSubtotal(),
                        "discountAmount", reg.getDiscountAmount(),
                        "finalAmount", reg.getFinalAmount(),
                        "currency", reg.getCurrency()));

        auditLogService.record(null, "POLICY_PENDING_REVIEW", "AD_POLICY", policy.getId(),
                Map.of("status", AdPolicyStatus.INACTIVE),
                Map.of("status", AdPolicyStatus.PENDING_REVIEW,
                        "advertisementId", ad.getId(),
                        "advertisementRef", ad.getAdvertisementRef().toString(),
                        "quoteRef", reg.getQuoteRef()));

        quote.completeStep(5);

        log.info("Fulfilled order {}: ad={}, policy={}, payment={}",
                reg.getOrderId(), ad.getId(), policy.getId(), payment.getId());
    }

    /* ------------------------- tx 1: unchanged ------------------------- */

    @Transactional
    public void insertNotification(Map<String, String> p) {
        PayHerePaymentNotification n = new PayHerePaymentNotification();
        n.setMerchantId(p.get("merchant_id"));
        n.setOrderId(p.get("order_id"));
        n.setPaymentId(p.get("payment_id"));
        n.setPayhereAmount(new BigDecimal(p.get("payhere_amount")));
        n.setCapturedAmount(new BigDecimal(p.get("captured_amount")));
        n.setPayhereCurrency(p.get("payhere_currency"));
        n.setStatusCode(Integer.parseInt(p.get("status_code")));
        n.setStatusMessage(truncate(p.get("status_message"), 500));
        n.setMethod(p.get("method"));
        n.setCardNo(p.get("card_no"));
        n.setRecurring(p.get("recurring"));
        n.setCustom1(p.get("custom_1"));
        n.setCustom2(p.get("custom_2"));
        n.setMd5sig(p.get("md5sig"));
        n.setProcessingStatus(NotificationProcessingStatus.RECEIVED);
        n.setProcessedAt(LocalDateTime.now());
        notificationRepository.save(n);

    }

    private static Optional<Long> parseQuoteRef(String orderId) {
        if (orderId == null) return Optional.empty();
        Matcher m = FORMAT.matcher(orderId);
        if (!m.matches()) return Optional.empty();
        try {
            return Optional.of(Long.parseLong(m.group(1)));
        } catch (NumberFormatException e) {   // digits too long for a long
            return Optional.empty();
        }
    }

    private void markIfPending(PaymentRegistry reg, PaymentRegistryStatus s) {
        if (reg.getStatus() == PaymentRegistryStatus.PENDING) reg.setStatus(s);
    }

    /* ------------------------------ ledger ------------------------------ */

    /**
     * Double entry. Debits = credits = subtotal.
     *   DR PAYHERE_CLEARING      finalAmount     (cash owed to us by the gateway)
     *   DR DISCOUNT_GIVEN        discountAmount  (contra-revenue, only if > 0)
     *   CR UNEARNED_AD_REVENUE   subtotal        (liability until the ad is approved/served)
     *
     * Policy is left null here; it is linked in handleFulfilment.
     */
    private void postLedger(PaymentRegistry reg, LocalDateTime now) {
        LedgerTransaction tx = new LedgerTransaction();
        tx.setTransactionType("AD_PURCHASE");
        tx.setReferenceType(REF_PAYMENT_REGISTRY);
        tx.setReferenceId(reg.getId());
        tx.setDescription("PayHere order " + reg.getOrderId());
        tx.setCreatedAt(now);
        ledgerTransactionRepository.save(tx);

        entry(tx, ACC_CLEARING, LedgerEntryType.DEBIT, reg.getFinalAmount());
        if (reg.getDiscountAmount().signum() > 0) {
            entry(tx, ACC_DISCOUNT, LedgerEntryType.DEBIT, reg.getDiscountAmount());
        }
        entry(tx, ACC_UNEARNED, LedgerEntryType.CREDIT, reg.getSubtotal());
    }

    private void entry(LedgerTransaction tx, String accountCode, LedgerEntryType type, BigDecimal amount) {
        LedgerAccount acc = ledgerAccountRepository.findByCodeAndActiveTrue(accountCode)
                .orElseThrow(() -> new IllegalStateException("Ledger account missing: " + accountCode));
        LedgerEntry e = new LedgerEntry();
        e.setTransaction(tx);
        e.setAccount(acc);
        e.setEntryType(type);
        e.setAmount(amount);
        ledgerEntryRepository.save(e);
    }

    /* ------------------------------ helpers ------------------------------ */

    private List<AdvertisementTier> tiersFromSnapshot(String json) {
        try {
            List<Map<String, Object>> lines = objectMapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
            List<Long> ids = lines.stream()
                    .filter(l -> "ADD_ON".equals(l.get("kind")))
                    .map(l -> ((Number) l.get("id")).longValue())
                    .toList();
            return ids.isEmpty() ? List.of() : tierRepository.findAllById(ids);
        } catch (JacksonException e) {
            throw new IllegalStateException("Corrupt line item snapshot", e);
        }
    }

    private static LocalDateTime expiryFor(BillingPeriod period, LocalDateTime from) {
        return switch (period) {
            case MONTHLY -> from.plusMonths(1);
            case SIX_MONTHS -> from.plusMonths(6);
            case YEARLY -> from.plusYears(1);
        };
    }

    private static String buildSlug(String title, UUID ref) {
        String base = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        if (base.length() > 200) base = base.substring(0, 200);
        return (base.isEmpty() ? "ad" : base) + "-" + ref.toString().substring(0, 8);
    }

    private static String truncate(String s, int max) {
        return s == null ? null : (s.length() > max ? s.substring(0, max) : s);
    }
}