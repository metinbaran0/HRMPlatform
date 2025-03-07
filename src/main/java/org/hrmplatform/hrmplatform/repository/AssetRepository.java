package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset,Long> {
}