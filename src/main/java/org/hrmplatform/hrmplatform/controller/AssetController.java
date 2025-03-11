package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.AssetRequestDto;
import org.hrmplatform.hrmplatform.dto.response.AssetResponseDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Asset;
import org.hrmplatform.hrmplatform.service.AssetService;
import org.hrmplatform.hrmplatform.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(ASSET)
@RequiredArgsConstructor
@CrossOrigin("*")
public class AssetController {
	
	private final AssetService assetService;
	private final AuthService authService;
	
	/**
	 * Zimmet ekleme işlemi - Yalnızca şirket yöneticisi ekleme yapabilir.
	 */
	@Transactional
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@PostMapping(ADD_ASSET)
	public ResponseEntity<BaseResponse<AssetResponseDto>> addAsset(
			@RequestHeader("Authorization") String token,
			@RequestBody AssetRequestDto assetRequest) {
		Long companyId = authService.getCompanyIdFromToken(token);
		AssetResponseDto savedAsset = assetService.addAsset(companyId, assetRequest);
		return ResponseEntity.ok(
				new BaseResponse<>(true, "Zimmet başarıyla eklendi.", 200, savedAsset)
		);
	}
	
	/**
	 * Şirketin tüm zimmetlerini getirir - Yalnızca şirket yöneticisi görebilir.
	 */
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('COMPANY_ADMIN')")
	@GetMapping(GET_ALL_ASSETS)
	public ResponseEntity<BaseResponse<List<AssetResponseDto>>> getAllAssets(@RequestHeader("Authorization") String token) {
		Long companyId = authService.getCompanyIdFromToken(token);
		List<AssetResponseDto> assets = assetService.getAllAssetsByCompany(companyId);
		return ResponseEntity.ok(
				new BaseResponse<>(true, "Tüm zimmetler başarıyla getirildi.", 200, assets)
		);
	}
	
	
	/**
	 * Zimmet bilgisi ID'ye göre getirilir - Çalışan sadece kendi zimmetini görebilir.
	 */
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@GetMapping(GET_ASSET_BY_ID)
	public ResponseEntity<BaseResponse<AssetResponseDto>> getAssetById(
			@RequestHeader("Authorization") String token, @PathVariable Long id) {
		Long companyId = authService.getCompanyIdFromToken(token);
		Long employeeId = authService.getEmployeeIdFromToken(token); // Çalışan ID'si alınıyor
		
		// Çalışanın ait olduğu şirketi doğrulamak
		if (!assetService.isEmployeeOfCompany(employeeId, companyId)) {
			return ResponseEntity.status(403).body(
					new BaseResponse<>(false, "Bu zimmete erişim yetkiniz yok.", 403, null)
			);
		}
		
		// Şirket yöneticisi veya zimmetin sahibi çalışan olmalı
		if (authService.isCompanyAdmin(token) || assetService.isEmployeeAsset(id, token)) {
			Optional<AssetResponseDto> assetResponse = assetService.getAssetById(id);
			return assetResponse.map(asset -> ResponseEntity.ok(
					                    new BaseResponse<>(true, "Zimmet başarıyla getirildi.", 200, asset)
			                    ))
			                    .orElseGet(() -> ResponseEntity.status(404).body(
					                    new BaseResponse<>(false, "Zimmet bulunamadı.", 404, null)
			                    ));
		} else {
			return ResponseEntity.status(403).body(
					new BaseResponse<>(false, "Bu zimmete erişim yetkiniz yok.", 403, null)
			);
		}
	}
	
	/**
	 * Zimmet güncellenir - Yalnızca şirket yöneticisi güncelleyebilir.
	 */
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@PatchMapping(UPDATE_ASSET)
	public ResponseEntity<BaseResponse<AssetResponseDto>> updateAsset(
			@RequestHeader("Authorization") String token,
			@PathVariable Long id,
			@RequestBody AssetRequestDto assetRequest) {
		authService.getCompanyIdFromToken(token);
		AssetResponseDto updatedAsset = assetService.updateAsset(id, assetRequest);
		return ResponseEntity.ok(
				new BaseResponse<>(true, "Zimmet başarıyla güncellendi.", 200, updatedAsset)
		);
	}
	
	/**
	 * Zimmet silinir - Yalnızca şirket yöneticisi silebilir.
	 */
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@DeleteMapping(DELETE_ASSET)
	public ResponseEntity<BaseResponse<Void>> deleteAsset(
			@RequestHeader("Authorization") String token, @PathVariable Long id) {
		authService.getCompanyIdFromToken(token);
		assetService.deleteAsset(id);
		return ResponseEntity.ok(
				new BaseResponse<>(true, "Zimmet başarıyla silindi.", 200, null)
		);
	}
	
	/**
	 * Şirkete ait tüm zimmetler getirilir - Yalnızca şirket yöneticisi erişebilir.
	 */
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@GetMapping("/company/{companyId}")
	public ResponseEntity<?> getAssetsByCompany(@RequestHeader("Authorization") String token, @PathVariable Long companyId) {
		authService.getCompanyIdFromToken(token);
		List<Asset> assets = assetService.getAssetsByCompany(companyId);
		return ResponseEntity.ok(assets);
	}
}