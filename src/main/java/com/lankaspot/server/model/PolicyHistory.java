package com.lankaspot.server.model;

import com.lankaspot.server.enums.PolicyHistoryAction;
import com.lankaspot.server.enums.AdSource;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "policy_histories")
public class PolicyHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "predecessor_policy_id", nullable = false, updatable = false)
    private AdPolicy predecessorPolicy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "successor_policy_id", nullable = false, updatable = false)
    private AdPolicy successorPolicy;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30, updatable = false)
    private PolicyHistoryAction actionType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(name = "reason", columnDefinition = "TEXT", updatable = false)
    private String reason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING) @Column(name="source",nullable=false,length=20)     private AdSource source=AdSource.USER;

    protected PolicyHistory() {
    }

    public PolicyHistory(AdPolicy predecessorPolicy, AdPolicy successorPolicy,
                         PolicyHistoryAction actionType, User actor, String reason) {
        this(predecessorPolicy, successorPolicy, actionType, actor, reason,
                successorPolicy == null ? AdSource.USER : successorPolicy.getSource());
    }

    public PolicyHistory(AdPolicy predecessorPolicy, AdPolicy successorPolicy,
                         PolicyHistoryAction actionType, User actor, String reason, AdSource source) {
        this.predecessorPolicy = predecessorPolicy;
        this.successorPolicy = successorPolicy;
        this.actionType = actionType;
        this.actor = actor;
        this.reason = reason;
        this.source = source == null ? AdSource.USER : source;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public AdPolicy getPredecessorPolicy() { return predecessorPolicy; }
    public AdPolicy getSuccessorPolicy() { return successorPolicy; }
    public PolicyHistoryAction getActionType() { return actionType; }
    public User getActor() { return actor; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public AdSource getSource(){return source;}
}
