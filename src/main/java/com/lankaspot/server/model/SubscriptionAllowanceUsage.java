package com.lankaspot.server.model;
import com.lankaspot.server.enums.AllowanceUsageStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="subscription_allowance_usage",uniqueConstraints=@UniqueConstraint(name="uq_allowance_usage_key",columnNames={"subscription_id","idempotency_key"}))
public class SubscriptionAllowanceUsage {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="subscription_id") private UserSubscription subscription;
 @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="tier_id") private AdvertisementTier tier;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="ad_policy_id") private AdPolicy adPolicy;
 @Column(name="period_start",nullable=false) private LocalDateTime periodStart;
 @Column(name="period_end",nullable=false) private LocalDateTime periodEnd;
 @Column(nullable=false) private int quantity=1;
 @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private AllowanceUsageStatus status=AllowanceUsageStatus.RESERVED;
 @Column(name="idempotency_key",nullable=false,length=150) private String idempotencyKey;
 public SubscriptionAllowanceUsage(UserSubscription s,AdvertisementTier t,AdPolicy p,LocalDateTime start,LocalDateTime end,String key){subscription=s;tier=t;adPolicy=p;periodStart=start;periodEnd=end;idempotencyKey=key;}
 public SubscriptionAllowanceUsage(UserSubscription s,AdvertisementTier t,AdPolicy p,LocalDateTime start,LocalDateTime end,String key,int quantity){this(s,t,p,start,end,key);this.quantity=quantity;}
 protected SubscriptionAllowanceUsage(){}
 public AllowanceUsageStatus getStatus(){return status;} public void setStatus(AllowanceUsageStatus s){status=s;}
 public Long getId(){return id;} public UserSubscription getSubscription(){return subscription;}
 public AdvertisementTier getTier(){return tier;} public AdPolicy getAdPolicy(){return adPolicy;}
 public LocalDateTime getPeriodStart(){return periodStart;} public LocalDateTime getPeriodEnd(){return periodEnd;}
 public String getIdempotencyKey(){return idempotencyKey;}
 public int getQuantity(){return quantity;}
}
