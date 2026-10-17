package com.lankaspot.server.repository;

import com.lankaspot.server.model.DiscountCodeUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeUsageRepository extends JpaRepository<DiscountCodeUsage, Long> {
    boolean existsByDiscountCode_IdAndUser_KeycloakUserId(Long discountCodeId, String keycloakUserId);
    boolean existsByDiscountCode_IdAndUser_Id(Long id, String userId);

}
