package com.lankaspot.server.model;

import com.lankaspot.server.enums.AdPolicyStatus;
import com.lankaspot.server.enums.AdSource;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ad_policies", indexes = {
        @Index(name = "idx_ad_policy_advertisement", columnList = "advertisement_id"),
        @Index(name = "idx_ad_policy_status", columnList = "status")
})
public class AdPolicy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "LKR";
    @Column(name = "discount_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private AdSource source = AdSource.USER;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tier_id", nullable = false)
    private AdvertisementTier tier;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AdPolicyStatus status = AdPolicyStatus.INACTIVE;
    @Column(name = "published_at") private LocalDateTime publishedAt;
    @Column(name = "expires_at") private LocalDateTime expiresAt;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false) private LocalDateTime updatedAt;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "advertisement_id") private Advertisement advertisement;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "approved_by") private User approvedBy;
    @Column(name = "approved_at") private LocalDateTime approvedAt;
    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY) private List<Payment> payments = new ArrayList<>();
    @OneToMany(mappedBy = "policy", fetch = FetchType.LAZY) private List<LedgerTransaction> ledgerTransactions = new ArrayList<>();

    protected AdPolicy() {}
    public AdPolicy(BigDecimal price, String currency, BigDecimal discountAmount, AdvertisementTier tier) {
        this.price = price; this.currency = currency;
        this.discountAmount = discountAmount == null ? BigDecimal.ZERO : discountAmount;
        this.tier = tier; this.createdAt = LocalDateTime.now(); this.updatedAt = this.createdAt;
    }
    @PrePersist void initializeTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }
    @PreUpdate void updateTimestamp() { updatedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public BigDecimal getPrice() { return price; }
    public String getCurrency() { return currency; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public AdSource getSource() { return source; }
    public void setSource(AdSource source) { this.source = source == null ? AdSource.USER : source; }
    public AdvertisementTier getTier() { return tier; }
    public AdPolicyStatus getStatus() { return status; }
    public void setStatus(AdPolicyStatus status) { this.status = status; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime value) { publishedAt = value; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime value) { expiresAt = value; }
    public Advertisement getAdvertisement() { return advertisement; }
    public void setAdvertisement(Advertisement value) { advertisement = value; }
    public User getApprovedBy() { return approvedBy; }
    public void approve(User reviewer, LocalDateTime decisionTime) {
        approvedBy = reviewer; approvedAt = decisionTime; status = AdPolicyStatus.APPROVED;
    }
    public LocalDateTime getApprovedAt() { return approvedAt; }
}
