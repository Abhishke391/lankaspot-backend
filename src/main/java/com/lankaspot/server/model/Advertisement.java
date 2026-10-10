package com.lankaspot.server.model;

import com.lankaspot.server.enums.AdvertisementStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "advertisements")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertisement_ref", nullable = false, unique = true, updatable = false)
    private UUID advertisementRef = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertiser_id", nullable = false)
    private User advertiser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "slug", nullable = false, unique = true, length = 250)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", precision = 15, scale = 2)
    private java.math.BigDecimal price;

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "area", length = 100)
    private String area;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AdvertisementStatus status;

    @Column(name = "published_at")
    private java.time.LocalDateTime publishedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_tier_id")
    private AdvertisementTier currentTier;

    @Column(name = "queue_number", nullable = false)
    private Long queueNumber;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void initializeTimestamps() {
        LocalDateTime now = LocalDateTime.now();
        if (advertisementRef == null) advertisementRef = UUID.randomUUID();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
        if (queueNumber == null) queueNumber = 0L;
    }

    @PreUpdate
    void updateTimestamp() { updatedAt = LocalDateTime.now(); }

    public AdvertisementStatus getStatus() { return status; }
    public void setStatus(AdvertisementStatus status) { this.status = status; }
    public Long getId() { return id; }
    public UUID getAdvertisementRef() { return advertisementRef; }
    public User getAdvertiser() { return advertiser; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public java.math.BigDecimal getPrice() { return price; }
    public String getDistrict() { return district; }
    public String getCity() { return city; }
    public String getArea() { return area; }
    public String getAddress() { return address; }
    public java.time.LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(java.time.LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public AdvertisementTier getCurrentTier() { return currentTier; }
    public void setCurrentTier(AdvertisementTier currentTier) { this.currentTier = currentTier; }
    public Long getQueueNumber() { return queueNumber; }
    public void setQueueNumber(Long queueNumber) { this.queueNumber = queueNumber; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Category getCategory(){return category;}
}