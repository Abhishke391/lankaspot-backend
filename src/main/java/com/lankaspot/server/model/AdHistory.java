package com.lankaspot.server.model;

import com.lankaspot.server.enums.AdHistoryAction;
import com.lankaspot.server.enums.AdSource;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ad_histories", indexes = @Index(name = "idx_ad_history_ad_ref", columnList = "advertisement_ref"))
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

    protected AdHistory() {
    }

    public AdHistory(UUID advertisementRef, AdQuote oldQuote, AdQuote newQuote,
                     AdHistoryAction actionType, User actor, String reason) {
        this(advertisementRef, oldQuote, newQuote, actionType, actor, reason,
                newQuote == null ? AdSource.USER : newQuote.getSource());
    }

    public AdHistory(UUID advertisementRef, AdQuote oldQuote, AdQuote newQuote,
                     AdHistoryAction actionType, User actor, String reason, AdSource source) {
        this.advertisementRef = advertisementRef;
        this.oldQuote = oldQuote;
        this.newQuote = newQuote;
        this.actionType = actionType;
        this.actor = actor;
        this.reason = reason;
        this.source = source == null ? AdSource.USER : source;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public UUID getAdvertisementRef() { return advertisementRef; }
    public AdQuote getOldQuote() { return oldQuote; }
    public AdQuote getNewQuote() { return newQuote; }
    public AdHistoryAction getActionType() { return actionType; }
    public User getActor() { return actor; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public AdSource getSource(){return source;}
}
