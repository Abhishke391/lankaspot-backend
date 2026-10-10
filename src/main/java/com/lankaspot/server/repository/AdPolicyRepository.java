package com.lankaspot.server.repository;
import com.lankaspot.server.model.AdPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AdPolicyRepository extends JpaRepository<AdPolicy, Long> {
    java.util.List<AdPolicy> findByAdvertisementAdvertiserIdOrderByCreatedAtDesc(Long advertiserId);
}
