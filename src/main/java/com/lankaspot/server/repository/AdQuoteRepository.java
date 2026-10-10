package com.lankaspot.server.repository;
import com.lankaspot.server.model.AdQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;
public interface AdQuoteRepository extends JpaRepository<AdQuote, Long> {
    List<AdQuote> findByAdvertisementRefOrderByVersionAsc(UUID advertisementRef);
    @Query("select q from AdQuote q where not exists " +
            "(select m.id from EntityMapping m where m.quote = q)")
    List<AdQuote> findUnconverted();
}
