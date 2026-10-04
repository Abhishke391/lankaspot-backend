package com.lankaspot.server.repository;

import com.lankaspot.server.model.LedgerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerAccountRepository extends JpaRepository<LedgerAccount, Long> {
}
