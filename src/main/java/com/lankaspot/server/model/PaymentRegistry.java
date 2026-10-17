package com.lankaspot.server.model;

import com.lankaspot.server.enums.PaymentRegistryStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "payment_registry",
        indexes = {
                @Index(name = "idx_payment_registry_quote_ref", columnList = "quote_ref"),
                @Index(name = "idx_payment_registry_payer", columnList = "payer_id")
        }
)
@Data
public class PaymentRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Sent to PayHere as order_id and returned to the frontend as invoiceNumber. Unique per attempt. */
    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    @Column(name = "quote_ref", nullable = false)
    private Long quoteRef;

    @Column(name = "payer_id", nullable = false, updatable = false)
    private UUID payerId;                       // Keycloak user id, same as AdQuote.advertiserId

    // replace the discount fields
    @Column(name = "discount_code_id")
    private Long discountCodeId;

    @Column(name = "discount_code", length = 50)
    private String discountCode;

    @Column(name = "hash_code", length = 100)
    private String hashCode;

    /* ---------- Pricing snapshot (what the hash was signed over) ---------- */

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    /** JSON of the plan + tier lines with the prices at checkout time. Prices can change before the webhook. */
    @Column(name = "line_items_snapshot", nullable = false, columnDefinition = "TEXT")
    private String lineItemsSnapshot;

    @Column(name = "subtotal", nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;


    @Column(name = "discount_percentage", precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /** The exact amount signed and sent to PayHere. The webhook must match this. */
    @Column(name = "final_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal finalAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "LKR";

    @Column(name = "billing_name", nullable = false, length = 200)
    private String billingName;

    @Column(name = "billing_email", nullable = false, length = 255)
    private String billingEmail;

    @Column(name = "listing_title", nullable = false, length = 200)
    private String listingTitle;

    /* ---------- Lifecycle ---------- */

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentRegistryStatus status = PaymentRegistryStatus.PENDING;

    /* ---------- Filled by the notify webhook ---------- */

    @Column(name = "payhere_payment_id", length = 100)
    private String payherePaymentId;

    @Column(name = "payhere_status_code")
    private Integer payhereStatusCode;

    @Column(name = "status_message", length = 500)
    private String statusMessage;

    @Column(name = "payhere_method", length = 30)
    private String payhereMethod;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /** Set once fulfilment has created the Advertisement, Policy and Payment. Null means not fulfilled yet. */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", unique = true)
    private Payment payment;

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}