package org.hrmplatform.hrmplatform.controller;

import org.hrmplatform.hrmplatform.dto.request.AssetRequestDto;
import org.hrmplatform.hrmplatform.dto.response.AssetResponseDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Asset;
import org.hrmplatform.hrmplatform.service.AssetService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

/**
 * Zimmet yönetimi ile ilgili API uç noktalarını sağlayan denetleyici sınıfıdır.
 * Kullanıcılar zimmet ekleyebilir, güncelleyebilir, silebilir ve mevcut zimmetleri listeleyebilir.
 */
@RestController
@RequestMapping(ASSET)
@CrossOrigin("*")
public class AssetController {
	private final AssetService assetService;
	
	public AssetController(AssetService assetService) {
		this.assetService = assetService;
	}
	
	/**
	 * Yeni bir zimmet ekler.
	 *
	 * @param assetRequest Zimmet eklemek için gerekli verileri içeren DTO
	 * @return Eklenen zimmet bilgilerini içeren cevap
	 */
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@PostMapping(ADD_ASSET)
	public ResponseEntity<BaseResponse<AssetResponseDto>> addAsset(@RequestBody AssetRequestDto assetRequest) {
		AssetResponseDto savedAsset = assetService.addAsset(assetRequest);
		return ResponseEntity.ok(
				BaseResponse.<AssetResponseDto>builder()
				            .code(200)
				            .success(true)
				            .data(savedAsset)
				            .message("Zimmet başarıyla eklendi.")
				            .build()
		);
	}
	
	/**
	 * Sistemde kayıtlı tüm zimmetleri getirir.
	 *
	 * @return Tüm zimmetleri içeren liste
	 */
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('COMPANY_ADMIN')")
	@GetMapping(GET_ALL_ASSETS)
	public ResponseEntity<BaseResponse<List<AssetResponseDto>>> getAllAssets() {
		List<AssetResponseDto> assets = assetService.getAllAssets();
		return ResponseEntity.ok(
				BaseResponse.<List<AssetResponseDto>>builder()
				            .code(200)
				            .success(true)
				            .data(assets)
				            .message("Tüm zimmetler başarıyla getirildi.")
				            .build()
		);
	}
	
	/**
	 * Belirtilen ID'ye sahip bir zimmeti getirir.
	 *
	 * @param id Zimmetin benzersiz kimliği
	 * @return İlgili zimmet bilgileri veya bulunamazsa 404 hatası
	 */
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('COMPANY_ADMIN')")
	@GetMapping(GET_ASSET_BY_ID)
	public ResponseEntity<BaseResponse<AssetResponseDto>> getAssetById(@PathVariable Long id) {
		Optional<AssetResponseDto> assetResponse = assetService.getAssetById(id);
		return assetResponse.map(asset -> ResponseEntity.ok(
				                    BaseResponse.<AssetResponseDto>builder()
				                                .code(200)
				                                .success(true)
				                                .data(asset)
				                                .message("Zimmet başarıyla getirildi.")
				                                .build()
		                    ))
		                    .orElseGet(() -> ResponseEntity.status(404).body(
				                    BaseResponse.<AssetResponseDto>builder()
				                                .code(404)
				                                .success(false)
				                                .message("Zimmet bulunamadı.")
				                                .build()
		                    ));
	}
	
	/**
	 * Belirtilen ID'ye sahip zimmeti günceller.
	 *
	 * @param id           Güncellenecek zimmetin benzersiz kimliği
	 * @param assetRequest Güncellenmiş zimmet bilgileri
	 * @return Güncellenmiş zimmet bilgileri içeren cevap
	 */
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@PatchMapping(UPDATE_ASSET)
	public ResponseEntity<BaseResponse<AssetResponseDto>> updateAsset(@PathVariable Long id, @RequestBody AssetRequestDto assetRequest) {
		AssetResponseDto updatedAsset = assetService.updateAsset(id, assetRequest);
		return ResponseEntity.ok(
				BaseResponse.<AssetResponseDto>builder()
				            .code(200)
				            .success(true)
				            .data(updatedAsset)
				            .message("Zimmet başarıyla güncellendi.")
				            .build()
		);
	}
	
	/**
	 * Belirtilen ID'ye sahip zimmeti siler.
	 *
	 * @param id Silinecek zimmetin benzersiz kimliği
	 * @return Silme işleminin başarılı olduğunu belirten cevap
	 */
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@DeleteMapping(DELETE_ASSET)
	public ResponseEntity<BaseResponse<Void>> deleteAsset(@PathVariable Long id) {
		assetService.deleteAsset(id);
		return ResponseEntity.ok(
				BaseResponse.<Void>builder()
				            .code(200)
				            .success(true)
				            .message("Zimmet başarıyla silindi.")
				            .build()
		);
	}
	
	@GetMapping("/company/{companyId}")
	public ResponseEntity<?> getAssetsByCompany(@PathVariable Long companyId) {
		List<Asset> assets = assetService.getAssetsByCompany(companyId);
		return ResponseEntity.ok(assets);
	}
}