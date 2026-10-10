package com.lankaspot.server.model;

import jakarta.persistence.*;

@Entity
@Table(name="subscription_allowances", uniqueConstraints=@UniqueConstraint(name="uq_allowance_subscription",columnNames="subscription_id"))
public class SubscriptionAllowance {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="subscription_id") private UserSubscription subscription;
    @Column(nullable=false) private int total;
    @Column(nullable=false) private int reserved;
    @Column(nullable=false) private int consumed;
    @Version private long version;
    protected SubscriptionAllowance(){}
    public SubscriptionAllowance(UserSubscription s,int total){subscription=s;this.total=total;}
    public UserSubscription getSubscription(){return subscription;} public int getAvailable(){return total-reserved-consumed;}
    public int getTotal(){return total;} public int getReserved(){return reserved;} public int getConsumed(){return consumed;}
    public void reserve(int n){if(n<=0||getAvailable()<n) throw new IllegalStateException("Insufficient subscription allowance");reserved+=n;}
    public void consume(int n){if(n<=0||reserved<n) throw new IllegalStateException("Reservation exceeds allowance");reserved-=n;consumed+=n;}
    public void release(int n){if(n<=0||reserved<n) throw new IllegalStateException("Invalid allowance release");reserved-=n;}
    public void reset(int n){total=n;reserved=0;consumed=0;}
}
