package com.lankaspot.server.repository;
import com.lankaspot.server.model.SubscriptionAllowanceUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface SubscriptionAllowanceUsageRepository extends JpaRepository<SubscriptionAllowanceUsage,Long>{
 Optional<SubscriptionAllowanceUsage> findBySubscriptionIdAndIdempotencyKey(Long subscriptionId,String key);
 long countBySubscriptionIdAndTierIdAndPeriodStartAndStatusIn(Long subscriptionId, Long tierId,
     java.time.LocalDateTime periodStart, java.util.Collection<com.lankaspot.server.enums.AllowanceUsageStatus> statuses);
}
