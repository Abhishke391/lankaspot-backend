package com.lankaspot.server.repository;

import com.lankaspot.server.model.AdQuoteSubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdQuoteSubscriptionPlanRepository extends JpaRepository<AdQuoteSubscriptionPlan, Long> {

    Optional<AdQuoteSubscriptionPlan> findByAdQuote_Id(Long quoteId);

}
