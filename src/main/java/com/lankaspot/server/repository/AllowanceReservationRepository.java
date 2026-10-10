package com.lankaspot.server.repository;
import com.lankaspot.server.model.AllowanceReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AllowanceReservationRepository extends JpaRepository<AllowanceReservation,Long>{Optional<AllowanceReservation> findBySubscriptionIdAndIdempotencyKey(Long id,String key);}
