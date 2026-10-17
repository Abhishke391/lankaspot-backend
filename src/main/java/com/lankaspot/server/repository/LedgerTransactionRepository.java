package com.lankaspot.server.repository;

import com.lankaspot.server.model.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {
    boolean existsByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
    Optional<LedgerTransaction> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}
