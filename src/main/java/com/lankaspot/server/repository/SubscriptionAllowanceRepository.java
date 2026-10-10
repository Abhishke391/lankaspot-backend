package com.lankaspot.server.repository;
import com.lankaspot.server.model.*;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface SubscriptionAllowanceRepository extends JpaRepository<SubscriptionAllowance,Long>{
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from SubscriptionAllowance a where a.subscription.id=:id") Optional<SubscriptionAllowance> lockBySubscriptionId(Long id);
}
