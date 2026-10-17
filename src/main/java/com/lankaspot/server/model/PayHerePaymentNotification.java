
package com.lankaspot.server.model;

import com.lankaspot.server.enums.NotificationProcessingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payhere_payment_notifications",
        indexes = {
                @Index(name = "idx_ph_notification_order_id", columnList = "order_id"),
                @Index(name = "idx_ph_notification_payment_id", columnList = "payment_id"),
                @Index(name = "idx_ph_notification_received_at", columnList = "received_at"),
                @Index(name = "idx_ph_notification_status_code", columnList = "status_code")
        }
)
@Data
@NoArgsConstructor
public class PayHerePaymentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false, length = 50)
    private String merchantId;

    @Column(name = "order_id", nullable = false, length = 100)
    private String orderId;

    @Column(name = "payment_id", nullable = false, length = 100)
    private String paymentId;

    @Column(name = "payhere_amount", precision = 19, scale = 2)
    private BigDecimal payhereAmount;

    @Column(name = "captured_amount", precision = 19, scale = 2)
    private BigDecimal capturedAmount;

    @Column(name = "payhere_currency", length = 3)
    private String payhereCurrency;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "status_message", length = 500)
    private String statusMessage;

    @Column(name = "method", length = 50)
    private String method;

    // Store only the masked card number returned by PayHere.
    @Column(name = "card_no", length = 32)
    private String cardNo;

    @Column(name = "recurring", length = 10)
    private String recurring;

    @Column(name = "custom_1", length = 255)
    private String custom1;

    @Column(name = "custom_2", length = 255)
    private String custom2;

    // Signature received from PayHere; retain for audit purposes.
    @Column(name = "md5sig", length = 128)
    private String md5sig;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 30)
    private NotificationProcessingStatus processingStatus =
            NotificationProcessingStatus.RECEIVED;

    @Column(name = "processing_error", length = 2000)
    private String processingError;

    @Column(name = "received_at", nullable = false, updatable = false)
    private LocalDateTime receivedAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

}
