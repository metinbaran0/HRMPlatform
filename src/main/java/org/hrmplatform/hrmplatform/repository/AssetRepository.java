package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.dto.response.AssetResponseDto;
import org.hrmplatform.hrmplatform.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset,Long> {
	List<Asset> findByCompanyId(Long companyId);
	Optional<Asset> findByIdAndCompanyId(Long id, Long companyId);
	boolean existsByIdAndEmployeeId(Long assetId, Long employeeId);

	
	
}