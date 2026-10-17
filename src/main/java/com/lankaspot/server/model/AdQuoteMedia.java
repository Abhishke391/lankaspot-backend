package com.lankaspot.server.model;

import com.lankaspot.server.enums.MediaStatus;
import com.lankaspot.server.enums.MediaType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ad_quote_media")
@Data
@NoArgsConstructor
public class AdQuoteMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ad_quote_id", nullable = false)
    private AdQuote adQuote;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaType mediaType;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_status", nullable = false, length = 20)
    private MediaStatus mediaStatus;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(name = "public_id", length = 255)
    private String publicId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "is_primary", nullable = false)
    private boolean primary = false;
}