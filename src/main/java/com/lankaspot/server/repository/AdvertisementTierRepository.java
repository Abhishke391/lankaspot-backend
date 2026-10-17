package com.lankaspot.server.repository;

import com.lankaspot.server.model.AdvertisementTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AdvertisementTierRepository extends JpaRepository<AdvertisementTier, Long> {

    List<AdvertisementTier> findByActiveTrueOrderByPriceAsc();
    List<AdvertisementTier> findByIdInAndActiveTrue(Collection<Long> ids);
    Optional<AdvertisementTier> findByCode(String code);

}
