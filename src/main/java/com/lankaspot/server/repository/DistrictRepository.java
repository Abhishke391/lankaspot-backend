package com.lankaspot.server.repository;

import com.lankaspot.server.model.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DistrictRepository extends JpaRepository<District, Long> {
}
