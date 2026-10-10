package com.lankaspot.server.model;
import jakarta.persistence.*;
import com.lankaspot.server.enums.BillingPeriod;
@Entity @Table(name="subscription_plan_tier_allowances",uniqueConstraints=@UniqueConstraint(name="uq_plan_tier_allowance",columnNames={"plan_id","tier_id"}))
public class SubscriptionPlanTierAllowance {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="plan_id") private SubscriptionPlan plan;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="tier_id") private AdvertisementTier tier;
 @Column(nullable=false) private int allowance;
 @Enumerated(EnumType.STRING) @Column(name="allowance_period",nullable=false,length=20)
 private BillingPeriod allowancePeriod = BillingPeriod.MONTHLY;
 protected SubscriptionPlanTierAllowance(){}
 public SubscriptionPlanTierAllowance(SubscriptionPlan p,AdvertisementTier t,int allowance){plan=p;tier=t;this.allowance=allowance;}
 public SubscriptionPlanTierAllowance(SubscriptionPlan p,AdvertisementTier t,int allowance,BillingPeriod period){this(p,t,allowance);allowancePeriod=period;}
 public Long getId(){return id;} public SubscriptionPlan getPlan(){return plan;} public AdvertisementTier getTier(){return tier;} public int getAllowance(){return allowance;}
 public BillingPeriod getAllowancePeriod(){return allowancePeriod;}
}
