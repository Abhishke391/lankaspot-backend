package com.lankaspot.server.repository;

import com.lankaspot.server.model.LedgerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long> {
    Optional<LedgerAccount> findByCodeAndActiveTrue(String code);
}
