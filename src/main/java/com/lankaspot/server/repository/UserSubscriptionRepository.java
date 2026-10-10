package com.lankaspot.server.repository;
import com.lankaspot.server.model.UserSubscription;
import com.lankaspot.server.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription,Long>{
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select s from UserSubscription s where s.id=:id")
 java.util.Optional<UserSubscription> findByIdForUpdate(Long id);
 @Query("select s from UserSubscription s where s.status=:status and s.autoRenew=true and s.nextRenewalAt<=:at") List<UserSubscription> findDue(SubscriptionStatus status,LocalDateTime at);
}
