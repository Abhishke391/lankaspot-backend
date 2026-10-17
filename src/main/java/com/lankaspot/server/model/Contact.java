package com.lankaspot.server.model;

import com.lankaspot.server.enums.ContactStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contacts",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_contact_user_phone",
                columnNames = {"user_id", "phone_number"}),
        indexes = @Index(name = "idx_contact_user", columnList = "user_id"))
@Getter
@Setter
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ContactStatus status = ContactStatus.NOT_VERIFIED;
}