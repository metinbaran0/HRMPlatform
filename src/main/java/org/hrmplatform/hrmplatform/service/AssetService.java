package org.hrmplatform.hrmplatform.service;

import org.hrmplatform.hrmplatform.dto.request.AssetRequestDto;
import org.hrmplatform.hrmplatform.dto.response.AssetResponseDto;
import org.hrmplatform.hrmplatform.entity.Asset;
import org.hrmplatform.hrmplatform.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssetService {
	private final AssetRepository assetRepository;
	
	public AssetService(AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}
	
	public AssetResponseDto addAsset(AssetRequestDto assetRequest) {
		Asset asset = new Asset();
		asset.setEmployeeId(assetRequest.employeeId());
		asset.setAssetName(assetRequest.assetName());
		asset.setAssetType(assetRequest.assetType());
		asset.setSerialNumber(assetRequest.serialNumber());
		asset.setAssignedDate(assetRequest.assignedDate());
		asset.setReturnDate(assetRequest.returnDate());
		
		Asset savedAsset = assetRepository.save(asset);
		return toResponse(savedAsset);
	}
	
	public List<AssetResponseDto> getAllAssets() {
		return assetRepository.findAll()
		                      .stream()
		                      .map(this::toResponse)
		                      .toList();
	}
	
	public Optional<AssetResponseDto> getAssetById(Long id) {
		return assetRepository.findById(id).map(this::toResponse);
	}
	
	public AssetResponseDto updateAsset(Long id, AssetRequestDto updatedAssetRequest) {
		Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
		asset.setAssetName(updatedAssetRequest.assetName());
		asset.setAssetType(updatedAssetRequest.assetType());
		asset.setSerialNumber(updatedAssetRequest.serialNumber());
		asset.setAssignedDate(updatedAssetRequest.assignedDate());
		asset.setReturnDate(updatedAssetRequest.returnDate());
		
		Asset updatedAsset = assetRepository.save(asset);
		return toResponse(updatedAsset);
	}
	
	public void deleteAsset(Long id) {
		assetRepository.deleteById(id);
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
	
	public List<Asset> getAssetsByCompany(Long companyId) {
		return assetRepository.findByCompanyId(companyId);
	}
}