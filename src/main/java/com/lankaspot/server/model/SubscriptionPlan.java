package com.lankaspot.server.model;

import com.lankaspot.server.enums.BillingPeriod;
import com.lankaspot.server.enums.AllowanceAllocationMode;
import com.lankaspot.server.enums.AllowanceResetPolicy;
import com.lankaspot.server.enums.SubscriptionPlanStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
@Data
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency = "LKR";

    @Column(name = "most_popular", nullable = false)
    private boolean mostPopular = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_period", nullable = false, length = 20)
    private BillingPeriod billingPeriod = BillingPeriod.MONTHLY;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscription_category_id", nullable = false)
    private SubscriptionCategory subscriptionCategory;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionPlanStatus status = SubscriptionPlanStatus.ACTIVE;


}