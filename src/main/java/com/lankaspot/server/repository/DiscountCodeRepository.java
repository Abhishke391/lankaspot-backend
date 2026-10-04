package com.lankaspot.server.repository;

import com.lankaspot.server.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
}
