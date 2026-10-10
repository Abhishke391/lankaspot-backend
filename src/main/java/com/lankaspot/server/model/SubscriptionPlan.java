package com.lankaspot.server.model;

import com.lankaspot.server.enums.BillingPeriod;
import com.lankaspot.server.enums.AllowanceAllocationMode;
import com.lankaspot.server.enums.AllowanceResetPolicy;
import com.lankaspot.server.enums.SubscriptionPlanStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 50) private String code;
    @Column(nullable = false, length = 100) private String name;
    @Column(columnDefinition="TEXT") private String description;
    @Column(nullable = false, precision = 15, scale = 2) private BigDecimal price;
    @Column(nullable = false, length = 3) private String currency = "LKR";
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private BillingPeriod billingPeriod = BillingPeriod.MONTHLY;
    @Column(nullable = false) private int advertisementAllowance;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length=20) private SubscriptionPlanStatus status = SubscriptionPlanStatus.DRAFT;
    @Column(name="validity_days", nullable=false) private int validityDays;
    @Enumerated(EnumType.STRING) @Column(name="reset_policy",nullable=false,length=20) private AllowanceResetPolicy resetPolicy = AllowanceResetPolicy.BILLING_PERIOD;
    @Enumerated(EnumType.STRING) @Column(name="allocation_mode",nullable=false,length=20) private AllowanceAllocationMode allocationMode=AllowanceAllocationMode.SHARED;
    protected SubscriptionPlan() {}
    public SubscriptionPlan(String code, String name, BigDecimal price, BillingPeriod period, int allowance) {
        this.code=code; this.name=name; this.price=price; this.billingPeriod=period; this.advertisementAllowance=allowance;
        this.validityDays = period == BillingPeriod.YEARLY ? 365 : 30;
    }
    public Long getId(){return id;} public String getCode(){return code;} public BigDecimal getPrice(){return price;}
    public String getCurrency(){return currency;} public BillingPeriod getBillingPeriod(){return billingPeriod;}
    public int getAdvertisementAllowance(){return advertisementAllowance;}     public boolean isActive(){return status == SubscriptionPlanStatus.ACTIVE;}
    public String getDescription(){return description;} public int getValidityDays(){return validityDays;}
    public AllowanceResetPolicy getResetPolicy(){return resetPolicy;} public AllowanceAllocationMode getAllocationMode(){return allocationMode;}
    public SubscriptionPlanStatus getStatus(){return status;}
}
