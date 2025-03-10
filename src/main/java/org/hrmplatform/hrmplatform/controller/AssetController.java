package org.hrmplatform.hrmplatform.controller;

import org.hrmplatform.hrmplatform.dto.request.AssetRequestDto;
import org.hrmplatform.hrmplatform.dto.response.AssetResponseDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Asset;
import org.hrmplatform.hrmplatform.entity.Company;
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
@CrossOrigin("*")
public class AssetController {
	private final AssetService assetService;
	private final AuthService authService;
	
	public AssetController(AssetService assetService, AuthService authService) {
		this.assetService = assetService;
		this.authService = authService;
	}
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
	
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('COMPANY_ADMIN')")
	@GetMapping(GET_ALL_ASSETS)
	public ResponseEntity<BaseResponse<List<AssetResponseDto>>> getAllAssets(@RequestHeader("Authorization") String token) {
		Long companyId = authService.getCompanyIdFromToken(token);
		List<AssetResponseDto> assets = assetService.getAllAssetsByCompany(companyId);
		return ResponseEntity.ok(
				new BaseResponse<>(true, "Tüm zimmetler başarıyla getirildi.", 200, assets)
		);
	}
	
	@PreAuthorize("hasRole('EMPLOYEE') or hasRole('COMPANY_ADMIN')")
	@GetMapping(GET_ASSET_BY_ID)
	public ResponseEntity<BaseResponse<AssetResponseDto>> getAssetById(
			@RequestHeader("Authorization") String token, @PathVariable Long id) {
		authService.getCompanyIdFromToken(token);
		Optional<AssetResponseDto> assetResponse = assetService.getAssetById(id);
		return assetResponse.map(asset -> ResponseEntity.ok(
				                    new BaseResponse<>(true, "Zimmet başarıyla getirildi.", 200, asset)
		                    ))
		                    .orElseGet(() -> ResponseEntity.status(404).body(
				                    new BaseResponse<>(false, "Zimmet bulunamadı.", 404, null)
		                    ));
	}
	
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
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
	
	@PreAuthorize("hasRole('COMPANY_ADMIN')")
	@DeleteMapping(DELETE_ASSET)
	public ResponseEntity<BaseResponse<Void>> deleteAsset(
			@RequestHeader("Authorization") String token, @PathVariable Long id) {
		authService.getCompanyIdFromToken(token);
		assetService.deleteAsset(id);
		return ResponseEntity.ok(
				new BaseResponse<>(true, "Zimmet başarıyla silindi.", 200, null)
		);
	}
	
	@GetMapping("/company/{companyId}")
	public ResponseEntity<?> getAssetsByCompany(@RequestHeader("Authorization") String token, @PathVariable Long companyId) {
		authService.getCompanyIdFromToken(token);
		List<Asset> assets = assetService.getAssetsByCompany(companyId);
		return ResponseEntity.ok(assets);
	}
}