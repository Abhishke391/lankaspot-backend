package com.lankaspot.server.repository;

import com.lankaspot.server.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    Optional<DiscountCode> findByCode(String code);

    @Modifying(flushAutomatically = true)
    @Query("update DiscountCode d set d.usedCount = d.usedCount + 1 where d.id = :id")
    int incrementUsedCount(@Param("id") Long id);

}
