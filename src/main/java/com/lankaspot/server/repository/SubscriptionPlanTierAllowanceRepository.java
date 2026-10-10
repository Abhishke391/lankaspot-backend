package com.lankaspot.server.repository;
import com.lankaspot.server.model.SubscriptionPlanTierAllowance;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SubscriptionPlanTierAllowanceRepository extends JpaRepository<SubscriptionPlanTierAllowance,Long>{
 java.util.Optional<SubscriptionPlanTierAllowance> findByPlanIdAndTierId(Long planId, Long tierId);
}
