package com.lankaspot.server.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import com.lankaspot.server.enums.EntityMappingType;

@Entity
@Table(name = "entity_mappings",
        uniqueConstraints = @UniqueConstraint(name = "uq_entity_mapping_quote", columnNames = "quote_id"),
        indexes = @Index(name = "idx_entity_mapping_policy", columnList = "policy_id"))
public class EntityMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quote_id", nullable = false)
    private AdQuote quote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private AdPolicy policy;

    @Enumerated(EnumType.STRING)
    @Column(name = "mapping_type", nullable = false, length = 30, updatable = false)
    private EntityMappingType mappingType = EntityMappingType.QUOTE_POLICY;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected EntityMapping() {
    }

    public EntityMapping(AdQuote quote, AdPolicy policy) {
        this.quote = quote;
        this.policy = policy;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    void initializeCreatedAt() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public AdQuote getQuote() { return quote; }
    public AdPolicy getPolicy() { return policy; }
    public EntityMappingType getMappingType() { return mappingType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
