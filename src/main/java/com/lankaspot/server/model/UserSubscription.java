package com.lankaspot.server.model;

import com.lankaspot.server.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="user_subscriptions", indexes=@Index(name="idx_subscription_due", columnList="status,next_renewal_at"))
public class UserSubscription {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="user_id") private User user;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="plan_id") private SubscriptionPlan plan;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private SubscriptionStatus status=SubscriptionStatus.ACTIVE;
    @Column(name="next_renewal_at",nullable=false) private LocalDateTime nextRenewalAt;
    @Column(name="period_start",nullable=false) private LocalDateTime periodStart;
    @Column(name="period_end",nullable=false) private LocalDateTime periodEnd;
    @Column(name="expires_at",nullable=false) private LocalDateTime expiresAt;
    @Column(name="purchased_price",nullable=false,precision=15,scale=2) private java.math.BigDecimal purchasedPrice;
    @Column(name="purchased_currency",nullable=false,length=3) private String purchasedCurrency;
    @Column(name="purchased_allowance",nullable=false) private int purchasedAllowance;
    @Column(name="auto_renew",nullable=false) private boolean autoRenew=true;
    @Column(name="approval_required",nullable=false) private boolean approvalRequired=true;
    @Column(name="created_at",nullable=false,updatable=false) private LocalDateTime createdAt=LocalDateTime.now();
    @Column(name="updated_at",nullable=false) private LocalDateTime updatedAt=LocalDateTime.now();
    @Column(name="cancelled_at") private LocalDateTime cancelledAt;
    @Column(name="cancellation_reason") private String cancellationReason;
    protected UserSubscription(){}
    public UserSubscription(User user, SubscriptionPlan plan, LocalDateTime nextRenewalAt){this.user=user;this.plan=plan;this.nextRenewalAt=nextRenewalAt;this.periodStart=nextRenewalAt.minusDays(plan.getValidityDays());this.periodEnd=nextRenewalAt;this.expiresAt=nextRenewalAt;this.purchasedPrice=plan.getPrice();this.purchasedCurrency=plan.getCurrency();this.purchasedAllowance=plan.getAdvertisementAllowance();}
    public Long getId(){return id;} public User getUser(){return user;} public SubscriptionPlan getPlan(){return plan;}
    public SubscriptionStatus getStatus(){return status;} public void setStatus(SubscriptionStatus s){status=s;}
    public LocalDateTime getNextRenewalAt(){return nextRenewalAt;} public void setNextRenewalAt(LocalDateTime d){nextRenewalAt=d;}
    public boolean isAutoRenew(){return autoRenew;} public boolean isApprovalRequired(){return approvalRequired;}
    public LocalDateTime getPeriodStart(){return periodStart;} public LocalDateTime getPeriodEnd(){return periodEnd;} public LocalDateTime getExpiresAt(){return expiresAt;}
    public int getPurchasedAllowance(){return purchasedAllowance;}
    public void setApprovalRequired(boolean v){approvalRequired=v;}
    public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;}
    public LocalDateTime getCancelledAt(){return cancelledAt;} public String getCancellationReason(){return cancellationReason;}
    public void cancel(String reason){status=SubscriptionStatus.CANCELLED;cancelledAt=LocalDateTime.now();cancellationReason=reason;}
}
