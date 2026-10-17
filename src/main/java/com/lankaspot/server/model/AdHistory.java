package com.lankaspot.server.model;

import com.lankaspot.server.enums.AdHistoryAction;
import com.lankaspot.server.enums.AdSource;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ad_histories", indexes = @Index(name = "idx_ad_history_ad_ref", columnList = "advertisement_ref"))
@Data
public class AdHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "advertisement_ref", nullable = false, updatable = false)
    private UUID advertisementRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "old_quote_id", nullable = false, updatable = false)
    private AdQuote oldQuote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "new_quote_id", nullable = false, updatable = false)
    private AdQuote newQuote;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30, updatable = false)
    private AdHistoryAction actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(name = "reason", columnDefinition = "TEXT", updatable = false)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING) @Column(name="source",nullable=false,length=20)     private AdSource source=AdSource.USER;

}
