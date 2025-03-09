package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepository extends JpaRepository<Asset,Long> {
	List<Asset> findByCompanyId(Long companyId);
}