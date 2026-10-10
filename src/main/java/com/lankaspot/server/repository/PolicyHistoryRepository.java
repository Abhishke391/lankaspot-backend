package com.lankaspot.server.repository;
import com.lankaspot.server.model.PolicyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PolicyHistoryRepository extends JpaRepository<PolicyHistory, Long> {
    java.util.List<PolicyHistory> findByPredecessorPolicyIdOrSuccessorPolicyIdOrderByCreatedAtAsc(Long predecessorId, Long successorId);
    List<PolicyHistory> findByPolicyIdOrderByCreatedAtAsc(Long policyId);
}
