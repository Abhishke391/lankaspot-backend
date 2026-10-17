package com.lankaspot.server.repository;

import com.lankaspot.server.model.AdQuoteMedia;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdQuoteMediaRepository extends JpaRepository<AdQuoteMedia, Long> {
    // AdQuoteMediaRepository
    List<AdQuoteMedia> findByAdQuoteIdOrderBySortOrderAsc(Long adQuoteId);
    List<AdQuoteMedia> findByAdQuote_IdOrderBySortOrderAscIdAsc(Long quoteId);

    long countByAdQuote_Id(Long quoteId);

    Optional<AdQuoteMedia> findByIdAndAdQuote_Id(Long mediaId, Long quoteId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select m from AdQuoteMedia m
            where m.adQuote.id = :quoteId
            order by m.sortOrder asc, m.id asc
            """)
    List<AdQuoteMedia> findAllByQuoteIdForUpdate(
            @Param("quoteId") Long quoteId
    );
}
