package com.lankaspot.server.repository;

import com.lankaspot.server.model.DiscountCodeUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeUsageRepository extends JpaRepository<DiscountCodeUsage, Long> {
}
