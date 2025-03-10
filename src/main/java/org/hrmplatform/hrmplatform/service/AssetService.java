package org.hrmplatform.hrmplatform.service;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.AssetRequestDto;
import org.hrmplatform.hrmplatform.dto.response.AssetResponseDto;
import org.hrmplatform.hrmplatform.entity.Asset;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.repository.AssetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssetService {
	private final AssetRepository assetRepository;
	private final AuthService authService;
	private final EmployeeService employeeService;
	
	@Transactional
	public AssetResponseDto addAsset(Long companyId, AssetRequestDto assetRequest) {
		// Çalışan adını kullanarak çalışan ID'sini bul
		Long employeeId = employeeService.getEmployeeIdByName(assetRequest.employeeName());
		
		if (employeeId == null) {
			throw new IllegalArgumentException("Çalışan bulunamadı: " + assetRequest.employeeName());
		}
		
		// Asset nesnesini oluştur
		Asset asset = new Asset();
		asset.setCompanyId(companyId);
		asset.setEmployeeId(employeeId);  // Burada employeeId'yi kullanıyoruz
		asset.setAssetName(assetRequest.assetName());
		asset.setAssetType(assetRequest.assetType());
		asset.setSerialNumber(assetRequest.serialNumber());
		asset.setAssignedDate(assetRequest.assignedDate());
		asset.setReturnDate(assetRequest.returnDate());
		
		// Asset'i kaydet
		Asset savedAsset = assetRepository.save(asset);
		return toResponse(savedAsset);
	}
	
	public List<AssetResponseDto> getAllAssetsByCompany(Long companyId) {
		return assetRepository.findByCompanyId(companyId)
		                      .stream()
		                      .map(this::toResponse)
		                      .toList();
	}
	
	public Optional<AssetResponseDto> getAssetById(Long id) {
		return assetRepository.findById(id).map(this::toResponse);
	}
	
	public AssetResponseDto updateAsset(Long id, AssetRequestDto updatedAssetRequest) {
		Asset asset = assetRepository.findById(id)
		                             .orElseThrow(() -> new RuntimeException("Asset not found or not authorized"));
		
		asset.setAssetName(updatedAssetRequest.assetName());
		asset.setAssetType(updatedAssetRequest.assetType());
		asset.setSerialNumber(updatedAssetRequest.serialNumber());
		asset.setAssignedDate(updatedAssetRequest.assignedDate());
		asset.setReturnDate(updatedAssetRequest.returnDate());
		
		Asset updatedAsset = assetRepository.save(asset);
		return toResponse(updatedAsset);
	}
	
	public void deleteAsset(Long id) {
		Asset asset = assetRepository.findById(id)
		                             .orElseThrow(() -> new RuntimeException("Asset not found or not authorized"));
		assetRepository.delete(asset);
	}
	
	public List<Asset> getAssetsByCompany(Long companyId) {
		return assetRepository.findByCompanyId(companyId);
	}
	
	private AssetResponseDto toResponse(Asset asset) {
		return new AssetResponseDto(
				asset.getId(),
				asset.getEmployeeId(),
				asset.getAssetName(),
				asset.getAssetType(),
				asset.getSerialNumber(),
				asset.getAssignedDate(),
				asset.getReturnDate()
				
		);
	}
}