package com.lankaspot.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seo_metadata")
public class SeoMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id", nullable = false, unique = true)
    private Advertisement advertisement;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Column(name = "meta_description", columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "canonical_url", length = 500)
    private String canonicalUrl;

    @Column(name = "robots_index", nullable = false)
    private boolean robotsIndex = true;

    @Column(name = "robots_follow", nullable = false)
    private boolean robotsFollow = true;
}