package com.lankaspot.server.model;

import com.lankaspot.server.enums.LedgerAccountType;
import jakarta.persistence.*;

@Entity
@Table(name = "ledger_accounts")
public class LedgerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 30)
    private LedgerAccountType accountType;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}