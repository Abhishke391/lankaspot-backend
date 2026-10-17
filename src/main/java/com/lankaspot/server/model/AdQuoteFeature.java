package com.lankaspot.server.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Mapping table between an AdQuote and the Features selected for it.
 * Rows are written once when the quote is created and never updated.
 */
@Entity
@Table(name = "ad_quote_features",
        uniqueConstraints = @UniqueConstraint(name = "uq_ad_quote_feature",
                columnNames = {"ad_quote_id", "feature_id"}),
        indexes = {
                @Index(name = "idx_aqf_ad_quote", columnList = "ad_quote_id"),
                @Index(name = "idx_aqf_feature", columnList = "feature_id")
        })
@Getter
@Setter
@NoArgsConstructor
public class AdQuoteFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_quote_id", nullable = false, updatable = false)
    private AdQuote adQuote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "feature_id", nullable = false, updatable = false)
    private Feature feature;

    /** Per-ad value, e.g. "3" for Bedrooms. Null for plain yes/no features. */
    @Column(name = "value", length = 100, updatable = false)
    private String value;

    public AdQuoteFeature(AdQuote adQuote, Feature feature, String value) {
        this.adQuote = adQuote;
        this.feature = feature;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdQuoteFeature other)) return false;
        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}