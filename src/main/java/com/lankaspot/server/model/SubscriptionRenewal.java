package com.lankaspot.server.model;

import jakarta.persistence.*;
import com.lankaspot.server.enums.AdSource;
import java.time.LocalDateTime;

@Entity
@Table(name="subscription_renewals", uniqueConstraints=@UniqueConstraint(name="uq_subscription_renewal_period", columnNames={"subscription_id","period_start"}))
public class SubscriptionRenewal {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="subscription_id") private UserSubscription subscription;
    @Column(name="period_start",nullable=false) private LocalDateTime periodStart;
    @Column(name="period_end",nullable=false) private LocalDateTime periodEnd;
    @Column(nullable=false,length=20) private String status;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private AdSource source=AdSource.SCHEDULER;
    protected SubscriptionRenewal(){}
    public SubscriptionRenewal(UserSubscription s,LocalDateTime start,LocalDateTime end,String status){subscription=s;periodStart=start;periodEnd=end;this.status=status;}
    public AdSource getSource(){return source;}
}
