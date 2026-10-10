package com.lankaspot.server.model;

import com.lankaspot.server.enums.AllowanceReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="allowance_reservations", uniqueConstraints=@UniqueConstraint(name="uq_allowance_idempotency",columnNames={"subscription_id","idempotency_key"}))
public class AllowanceReservation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="subscription_id") private UserSubscription subscription;
    @Column(name="idempotency_key",nullable=false,length=150) private String idempotencyKey;
    @Column(nullable=false) private int quantity;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private AllowanceReservationStatus status=AllowanceReservationStatus.RESERVED;
    @Column(nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    protected AllowanceReservation(){}
    public AllowanceReservation(UserSubscription s,String key,int quantity){subscription=s;idempotencyKey=key;this.quantity=quantity;}
    public Long getId(){return id;} public UserSubscription getSubscription(){return subscription;} public String getIdempotencyKey(){return idempotencyKey;}
    public int getQuantity(){return quantity;} public AllowanceReservationStatus getStatus(){return status;} public void setStatus(AllowanceReservationStatus s){status=s;}
}
