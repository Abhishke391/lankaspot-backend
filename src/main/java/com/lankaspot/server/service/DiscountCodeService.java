package com.lankaspot.server.service;

import com.lankaspot.server.exception.DiscountCodeNotValidException;
import com.lankaspot.server.exception.UserNotFoundException;
import com.lankaspot.server.model.DiscountCode;
import com.lankaspot.server.model.DiscountCodeUsage;
import com.lankaspot.server.model.Payment;
import com.lankaspot.server.model.User;
import com.lankaspot.server.repository.DiscountCodeRepository;
import com.lankaspot.server.repository.DiscountCodeUsageRepository;
import com.lankaspot.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountCodeService {

    private static final String DISCOUNT_INVALID_MESSAGE = "This discount code is invalid or has expired.";
    private static final String DISCOUNT_ALREADY_USED_MESSAGE = "You have already used this discount code.";

    private final DiscountCodeRepository discountCodeRepository;
    private final DiscountCodeUsageRepository discountCodeUsageRepository;
    private final UserRepository userRepository;

    public DiscountCode findApplicableDiscount(String code, String keycloakUserId) {
        DiscountCode discount = discountCodeRepository.findByCode(code)
                .filter(d -> d.getCode().equals(code))
                .orElseThrow(() -> new DiscountCodeNotValidException(DISCOUNT_INVALID_MESSAGE));

        LocalDateTime now = LocalDateTime.now();
        boolean usable = discount.isActive()
                && !discount.getValidFrom().isAfter(now)
                && (discount.getValidUntil() == null || discount.getValidUntil().isAfter(now))
                && (discount.getMaxUses() == null || discount.getUsedCount() < discount.getMaxUses());

        if (!usable) throw new DiscountCodeNotValidException(DISCOUNT_INVALID_MESSAGE);

        log.info("Discount code {} is valid for user {}", discount.getCode(), keycloakUserId);
        if (discountCodeUsageRepository.existsByDiscountCode_IdAndUser_KeycloakUserId(discount.getId(), keycloakUserId)) {
            throw new DiscountCodeNotValidException(DISCOUNT_ALREADY_USED_MESSAGE);
        }
        return discount;
    }

    @Transactional
    public void consume(Long discountCodeId, String userId, Payment payment) {
        // Idempotency / double-checkout guard: the same user could have paid two
        // checkouts that both used the code. The money is already taken, so don't fail.
        if (discountCodeUsageRepository.existsByDiscountCode_IdAndUser_KeycloakUserId(discountCodeId, userId)) {
            log.warn("Discount code {} already consumed by user {}; skipping usage record (payment {})",
                    discountCodeId, userId, payment.getId());
            return;
        }
        User user = userRepository.findByKeycloakUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
        DiscountCodeUsage usage = new DiscountCodeUsage();
        usage.setDiscountCode(discountCodeRepository.getReferenceById(discountCodeId));
        usage.setUser(user);
        usage.setPayment(payment);
        usage.setDiscountAmount(payment.getDiscountAmount());
        usage.setUsedAt(LocalDateTime.now());
        discountCodeUsageRepository.save(usage);

        discountCodeRepository.incrementUsedCount(discountCodeId);
    }
}