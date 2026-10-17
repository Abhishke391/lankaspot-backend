package com.lankaspot.server.model;

import jakarta.persistence.*;
import com.lankaspot.server.enums.AdSource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "subscription_renewals",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_subscription_renewal_period",
                columnNames = {"subscription_plan_id", "period_start"}
        )
)@Data
@NoArgsConstructor
public class SubscriptionRenewal {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Column(name="period_start",nullable=false)
    private LocalDateTime periodStart;

    @Column(name="period_end",nullable=false)
    private LocalDateTime periodEnd;

    @Column(nullable=false,length=20)
    private String status;

    @Column(nullable=false)
    private LocalDateTime createdAt=LocalDateTime.now();

    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20)
    private AdSource source=AdSource.USER;

}
