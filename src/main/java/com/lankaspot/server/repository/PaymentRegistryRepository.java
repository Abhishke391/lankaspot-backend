package com.lankaspot.server.repository;

import com.lankaspot.server.enums.PaymentRegistryStatus;
import com.lankaspot.server.model.PaymentRegistry;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRegistryRepository extends JpaRepository<PaymentRegistry, Long> {
    Optional<PaymentRegistry> findByOrderId(String orderId);

    //existsByOrderIdAndStatus
    boolean existsByOrderIdAndStatus(String orderId, PaymentRegistryStatus status);

    boolean existsByQuoteRefAndPayerIdAndStatus(Long quoteRef, UUID payerId, PaymentRegistryStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from PaymentRegistry r where r.orderId = :orderId")
    Optional<PaymentRegistry> findByOrderIdForUpdate(@Param("orderId") String orderId);


}