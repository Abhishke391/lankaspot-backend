package com.lankaspot.server.model;

import com.lankaspot.server.enums.AdQuoteStatus;
import com.lankaspot.server.enums.AdSource;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable content snapshot. Commercial terms belong to AdPolicy.
 */
@Entity
@Table(name = "ad_quotes",
        uniqueConstraints = @UniqueConstraint(name = "uq_ad_quote_version",
                columnNames = {"advertisement_ref", "version"}))
public class AdQuote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertisement_ref", nullable = false, updatable = false)
    private UUID advertisementRef;

    @Column(name = "version", nullable = false, updatable = false)
    private Integer version;

    @Column(name = "advertiser_id", updatable = false)
    private Long advertiserId;

    @Column(name = "category_id", updatable = false)
    private Long categoryId;

    @Column(name = "category_name", length = 100, updatable = false)
    private String categoryName;

    @Column(name = "title", nullable = false, length = 200, updatable = false)
    private String title;

    @Column(name = "slug", nullable = false, length = 250, updatable = false)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT", updatable = false)
    private String description;

    @Column(name = "price", precision = 15, scale = 2, updatable = false)
    private BigDecimal price;

    @Column(name = "district", nullable = false, length = 100, updatable = false)
    private String district;

    @Column(name = "city", nullable = false, length = 100, updatable = false)
    private String city;

    @Column(name = "area", length = 100, updatable = false)
    private String area;

    @Column(name = "address", columnDefinition = "TEXT", updatable = false)
    private String address;

    @Column(name = "media_references", columnDefinition = "TEXT", updatable = false)
    private String mediaReferences;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AdQuoteStatus status = AdQuoteStatus.IN_COMPLETE;
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    private AdSource source = AdSource.USER;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected AdQuote() {
    }

    public AdQuote(UUID advertisementRef, Integer version, String title, String slug,
                   String description, BigDecimal price, String district, String city,
                   String area, String address, String mediaReferences) {
        this.advertisementRef = advertisementRef;
        this.version = version;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.price = price;
        this.district = district;
        this.city = city;
        this.area = area;
        this.address = address;
        this.mediaReferences = mediaReferences;
        this.createdAt = LocalDateTime.now();
    }
    public void setSource(AdSource source) { this.source = source == null ? AdSource.USER : source; }
    public AdSource getSource() { return source; }

    @PrePersist
    void initializeCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public UUID getAdvertisementRef() { return advertisementRef; }
    public Integer getVersion() { return version; }
    public String getTitle() { return title; }
    public AdQuoteStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getSlug(){return slug;} public String getDescription(){return description;} public BigDecimal getPrice(){return price;}
    public String getDistrict(){return district;} public String getCity(){return city;} public String getArea(){return area;}
    public String getAddress(){return address;} public String getMediaReferences(){return mediaReferences;}
}
