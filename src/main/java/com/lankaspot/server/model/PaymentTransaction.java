package com.lankaspot.server.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "transaction_reference", nullable = false, unique = true, length = 255)
    private String transactionReference;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_status", nullable = false, length = 30)
    private String transactionStatus;

    @Column(name = "response_code", length = 100)
    private String responseCode;

    @Column(name = "response_message", length = 500)
    private String responseMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}