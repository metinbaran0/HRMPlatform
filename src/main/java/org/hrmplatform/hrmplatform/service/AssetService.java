package org.hrmplatform.hrmplatform.service;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.AssetRequestDto;
import org.hrmplatform.hrmplatform.dto.response.AssetResponseDto;
import org.hrmplatform.hrmplatform.entity.Asset;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.repository.AssetRepository;
import org.hrmplatform.hrmplatform.repository.EmployeeRepository;
import org.hrmplatform.hrmplatform.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {
	private final AssetRepository assetRepository;
	

	private final EmployeeService employeeService;
	
	// Zimmetin ID'sine göre detaylarını getiren metod
	public Optional<AssetResponseDto> getAssetById(Long id) {
		Optional<Asset> asset = assetRepository.findById(id);
		return asset.map(this::convertToAssetResponseDto);
	}
	
	// Çalışanın bir şirkete ait olup olmadığını kontrol et
	public boolean isEmployeeOfCompany(Long employeeId, Long companyId) {
		Optional<Employee> employee = employeeService.findById(employeeId);
		return employee.map(emp -> emp.getCompanyId().equals(companyId)).orElse(false);
	}
	

	// Çalışan adını kullanarak çalışan ID'sini bulmak
	public Long getEmployeeIdByEmail(String email) {
		Optional<Employee> employee = employeeService.findByEmail(email);
		if (employee.isPresent()) {
			return employee.get().getId();
		}
		return null;
	}
	
	// Çalışanın zimmetli varlığını kontrol eden metod
	public boolean isEmployeeAsset(Long assetId, String email) {
		Long employeeId = getEmployeeIdByEmail(email);
		if (employeeId == null) {
			throw new IllegalArgumentException("Çalışan bulunamadı: " + email);
		}
		// Asset ile çalışan ID'si eşleşiyorsa true döndürüyoruz
		return assetRepository.existsByIdAndEmployeeId(assetId, employeeId);
	}
	
	@Transactional
	public AssetResponseDto addAsset(Long companyId, AssetRequestDto assetRequest) {
		Long employeeId = employeeService.getEmployeeIdByEmail(assetRequest.employeeEmail());
		
		if (employeeId == null) {
			throw new IllegalArgumentException("Çalışan bulunamadı: " + assetRequest.employeeEmail());
		}
		
		
		// Asset nesnesini oluştur
		Asset asset = new Asset();
		asset.setCompanyId(companyId);
		asset.setEmployeeId(employeeId);
		asset.setAssetName(assetRequest.assetName());
		asset.setAssetType(assetRequest.assetType());
		asset.setSerialNumber(assetRequest.serialNumber());
		asset.setAssignedDate(assetRequest.assignedDate());
		asset.setReturnDate(assetRequest.returnDate());
		
		// Asset'i kaydet
		Asset savedAsset = assetRepository.save(asset);
		return toResponse(savedAsset);
	}
	
	// Tüm asset'leri almak
	public List<AssetResponseDto> getAllAssetsByCompany(Long companyId) {
		return assetRepository.findByCompanyId(companyId)
		                      .stream()
		                      .map(this::toResponse)
		                      .toList();
	}
	
	// Asset güncelleme işlemi
	public AssetResponseDto updateAsset(Long id, AssetRequestDto updatedAssetRequest) {
		Asset asset = assetRepository.findById(id)
		                             .orElseThrow(() -> new RuntimeException("Zimmet bulunamadı veya yetkiniz yok"));
		
		asset.setAssetName(updatedAssetRequest.assetName());
		asset.setAssetType(updatedAssetRequest.assetType());
		asset.setSerialNumber(updatedAssetRequest.serialNumber());
		asset.setAssignedDate(updatedAssetRequest.assignedDate());
		asset.setReturnDate(updatedAssetRequest.returnDate());
		
		Asset updatedAsset = assetRepository.save(asset);
		return toResponse(updatedAsset);
	}
	
	// Asset silme işlemi
	public void deleteAsset(Long id) {
		Asset asset = assetRepository.findById(id)
		                             .orElseThrow(() -> new RuntimeException("Zimmet bulunamadı veya yetkiniz yok"));
		
		assetRepository.delete(asset);
	}
	
	// Şirkete ait tüm asset'leri almak
	public List<Asset> getAssetsByCompany(Long companyId) {
		return assetRepository.findByCompanyId(companyId);
	}
	
	// Entity'yi DTO'ya çeviren yardımcı metod
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
	
	// Asset'i response DTO'ya dönüştürme
	private AssetResponseDto convertToAssetResponseDto(Asset asset) {
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