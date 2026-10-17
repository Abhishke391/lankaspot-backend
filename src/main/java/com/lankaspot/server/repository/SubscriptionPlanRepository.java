package com.lankaspot.server.repository;
import com.lankaspot.server.enums.SubscriptionPlanStatus;
import com.lankaspot.server.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan,Long>{
    Optional<SubscriptionPlan> findByCode(String code);
    List<SubscriptionPlan> findByStatusOrderByPriceAsc(SubscriptionPlanStatus status);
    Optional<SubscriptionPlan> findByIdAndStatus(Long id, SubscriptionPlanStatus status);

}
