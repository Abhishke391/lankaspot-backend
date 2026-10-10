package com.lankaspot.server.model;

import com.lankaspot.server.enums.ApprovalStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertisement_approvals")
public class AdvertisementApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "advertisement_id")
    private Advertisement advertisement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private AdPolicy policy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ApprovalStatus status;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "decision_at")
    private LocalDateTime decisionAt;

    public AdvertisementApproval() { }
    public AdvertisementApproval(AdPolicy policy, Advertisement advertisement, User reviewer, ApprovalStatus status, String reason) {
        this.policy = policy;
        this.advertisement = advertisement;
        this.reviewer = reviewer;
        this.status = status;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
        this.decisionAt = this.createdAt;
    }
    public Long getId() { return id; }
    public Advertisement getAdvertisement() { return advertisement; }
    public AdPolicy getPolicy() { return policy; }
    public User getReviewer() { return reviewer; }
    public ApprovalStatus getStatus() { return status; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
    public String getReason() { return reason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDecisionAt() { return decisionAt; }
    public void setAdvertisement(Advertisement advertisement) { this.advertisement = advertisement; }
}