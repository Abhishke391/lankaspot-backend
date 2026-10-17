package com.lankaspot.server.repository;

import com.lankaspot.server.model.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeatureRepository extends JpaRepository<Feature, Long> {

    @Query(value = """
            SELECT f.*
            FROM features f
            JOIN category_features cf ON cf.feature_id = f.id
            WHERE cf.category_id = :categoryId
            ORDER BY f.sort_order ASC, f.name ASC
            """, nativeQuery = true)
    List<Feature> findByCategoryId(@Param("categoryId") Long categoryId);
}
