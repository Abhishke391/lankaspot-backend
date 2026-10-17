package com.lankaspot.server.model;

import com.lankaspot.server.enums.SubscriptionCategoryStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "subscription_categories")
public class SubscriptionCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean defaultCategory = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubscriptionCategoryStatus status =
            SubscriptionCategoryStatus.DRAFT;
}