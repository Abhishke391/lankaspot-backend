package com.lankaspot.server.repository;

import com.lankaspot.server.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findAllByDistrictId(Long districtId);

}
