package com.lankaspot.server.repository;

import com.lankaspot.server.model.SeoMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeoMetadataRepository extends JpaRepository<SeoMetadata, Long> {
}
