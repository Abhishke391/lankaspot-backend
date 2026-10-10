package com.lankaspot.server.repository;
import com.lankaspot.server.model.EntityMapping;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EntityMappingRepository extends JpaRepository<EntityMapping, Long> {
    java.util.Optional<EntityMapping> findByQuoteId(Long quoteId);
    boolean existsByPolicyId(Long policyId);
}
