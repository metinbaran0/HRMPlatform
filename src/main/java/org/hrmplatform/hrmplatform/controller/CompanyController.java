package org.hrmplatform.hrmplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CompanyDto;
import org.hrmplatform.hrmplatform.dto.request.SubscriptionPlanRequest;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.dto.response.CompanySummaryResponseDto;
import org.hrmplatform.hrmplatform.dto.response.SubscriptionResponse;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.service.CompanyService;
import org.hrmplatform.hrmplatform.service.EmailService;
import org.hrmplatform.hrmplatform.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(COMPANY)
@RequiredArgsConstructor
@CrossOrigin("*")
@PreAuthorize("isAuthenticated()")
public class CompanyController {
	private final CompanyService companyService;
	private final UserService userService;
	private final EmailService emailService;
  //findbyname ve token işemleri yapılacak
    //bütün şirketleri görme
    @GetMapping(FINDALLCOMPANY)
    public ResponseEntity<BaseResponse<List<Company>>> findAllCompanies() {
        List<Company> companies = companyService.findAllCompanies(); // Şirket listesini al
        return ResponseEntity.ok(
                BaseResponse.<List<Company>>builder()
                        .code(200)
                        .data(companies) // Listeyi buraya ekliyoruz
                        .message("Şirketler başarıyla getirildi")
                        .success(true)
                        .build()//state içerisine kaydedererek
        );
    }

    //id'e göre şirket arama
    @GetMapping(FINDBYCOMPANYID + "/{id}")
    public ResponseEntity<BaseResponse<Company>> findByCompanyId(@PathVariable Long id) {
        Optional<Company> company = companyService.findByCompanyId(id);
        if (company.isPresent()) {
            return ResponseEntity.ok(
                    BaseResponse.<Company>builder()
                            .code(200)
                            .data(company.get())
                            .message("Şirket başarıyla getirildi")
                            .success(true)
                            .build()
            );

        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<Company>builder()
                            .code(ErrorType.DATA_NOT_FOUND.getCode())
                            .message(ErrorType.DATA_NOT_FOUND.getMessage())
                            .success(false)
                            .build());
        }

    }

    // Şirket ismine göre arama
    @GetMapping(FINDBYCOMPANYNAME + "/{name}")
    public ResponseEntity<BaseResponse<List<Company>>> findByCompanyName(@PathVariable String name) {
        List<Company> companies = companyService.findByCompanyName(name);
        if (!companies.isEmpty()) {
            return ResponseEntity.ok(
                    BaseResponse.<List<Company>>builder()
                            .code(200)
                            .data(companies)
                            .message("Şirket başarıyla getirildi")
                            .success(true)
                            .build()
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<List<Company>>builder()
                            .code(ErrorType.DATA_NOT_FOUND.getCode())
                            .message(ErrorType.COMPANY_NOT_FOUND.getMessage())
                            .success(false)
                            .build());
        }
    }

 
 

    //şirket güncelleme
    @PutMapping(UPDATECOMPANY + "/{id}")
    public ResponseEntity<BaseResponse<Void>> updateCompany(@PathVariable Long id, @RequestBody @Valid CompanyDto companyDto) {
        companyService.updateCompany(id, companyDto);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(200)
                .message("Şirket başarıyla güncellendi")
                .success(true)
                .build());
    }

    //şirket silme soft delete
    @DeleteMapping(DELETECOMPANY + "{id}")
    public ResponseEntity<BaseResponse<Void>> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(BaseResponse.<Void>builder()
                .code(200)
                .message("Şirket başarıyla silindi")
                .success(true)
                .build());
    }

    //şirket başvurularını görüntüleme
    @GetMapping(PENDING)
    public ResponseEntity<BaseResponse<List<Company>>> getPendingCompanies() {
        List<Company> pendingCompanies = companyService.getPendingCompanies();

        return ResponseEntity.ok(BaseResponse.<List<Company>>builder()
                .code(200)
                .message("Bekleyen şirket başvuruları getirildi")
                .success(true)
                .data(pendingCompanies)
                .build());
    }

    //şirket başvurusu onaylama
    @PutMapping(APPROVE + "/{id}")
    public ResponseEntity<BaseResponse<Company>> approveCompany(@PathVariable Long id, String token) {
        Company approvedCompany = companyService.approveCompany(id,token);

        return ResponseEntity.ok(BaseResponse.<Company>builder()
                .code(200)
                .message("Şirket başvurusu onaylandı")
                .success(true)
                .data(approvedCompany)
                .build());
    }

    //şirket başvurusu reddetme
    @PutMapping(REJECT + "/{id}")
    public ResponseEntity<BaseResponse<Company>> rejectCompany(@PathVariable Long id) {
        Company rejectedCompany = companyService.rejectCompany(id);

        return ResponseEntity.ok(BaseResponse.<Company>builder()
                .code(200)
                .message("Şirket başvurusu reddedildi")
                .success(true)
                .data(rejectedCompany)
                .build());
    }

    //üyelik planı gğncelleme
    @PostMapping(SUBSCRIPTION + "/{id}")
    public ResponseEntity<BaseResponse<Company>> setSubscriptionPlan(
            @PathVariable Long id,
            @RequestBody SubscriptionPlanRequest request
    ) {
        Company updatedCompany = companyService.setSubscriptionPlan(id, request.getPlan());
        return ResponseEntity.ok(
                BaseResponse.<Company>builder()
                        .code(200)
                        .message("Şirket üyelik planı güncellendi")
                        .success(true)
                        .data(updatedCompany)
                        .build()
        );
    }

    //bir şirketin üyelik planlanını getirir
    @GetMapping(SUBSCRIPTION + "/{id}")
    public ResponseEntity<BaseResponse<SubscriptionResponse>> getSubscriptionPlan(@PathVariable Long id) {
        SubscriptionResponse subscription = companyService.getSubscriptionPlan(id);
        return ResponseEntity.ok(
                BaseResponse.<SubscriptionResponse>builder()
                        .code(200)
                        .message("Şirket üyelik planı getirildi")
                        .success(true)
                        .data(subscription)
                        .build()
        );
    }

    //Üyelik Süresi Dolan Şirketin Erişimini Kısıtlama
    @PatchMapping(SUBSCRIPTION + "/{id}/expire")
    public ResponseEntity<BaseResponse<String>> expireSubscription(@PathVariable Long id) {
        companyService.expireSubscription(id);
        return ResponseEntity.ok(
                BaseResponse.<String>builder()
                        .code(200)
                        .message("Şirketin üyeliği sona erdirildi")
                        .success(true)
                        .data("Şirketin erişimi kısıtlandı")
                        .build()
        );
    }







	                //     METIN
	
	
	
	//Şirket, yöneticiler ve çalışan sayısını döner
	@GetMapping("/summary")
	public ResponseEntity<BaseResponse<CompanySummaryResponseDto>> getDashboardSummary() {
		CompanySummaryResponseDto summary = new CompanySummaryResponseDto(
				companyService.getTotalCompanyCount(),
				userService.getTotalAdminCount(),
				userService.getTotalEmployeeCount()
		);
		
		return ResponseEntity.ok(
				BaseResponse.<CompanySummaryResponseDto>builder()
				            .code(200)
				            .message("Dashboard summary retrieved successfully")
				            .success(true)
				            .data(summary)
				            .build()
		);
	}
	
	//Yaklaşan üyelik sonlandırma listesini döner
	@GetMapping("/subscriptions/expiring-soon")
	public ResponseEntity<BaseResponse<List<Company>>> getExpiringSoonCompanies() {
		List<Company> expiringSoonCompanies = companyService.getExpiringSoonCompanies();
		
		return ResponseEntity.ok(BaseResponse.<List<Company>>builder()
		                                     .code(200)
		                                     .message("Yaklaşan üyelik sonlandırma listesi başarıyla getirildi")
		                                     .success(true)
		                                     .data(expiringSoonCompanies)
		                                     .build());
	}
	
	//Kullanıcı hesabını pasif hale getirir
	@PatchMapping("/users/deactivate")
	public ResponseEntity<BaseResponse<String>> deactivateUser(@RequestParam Long userId) {
		try {
			companyService.deactivateUser(userId);
			return ResponseEntity.ok(BaseResponse.<String>builder()
			                                     .code(200)
			                                     .message("Kullanıcı başarıyla pasif hale getirildi")
			                                     .success(true)
			                                     .data("Kullanıcı hesabı pasif hale getirildi")
			                                     .build());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			                     .body(BaseResponse.<String>builder()
			                                       .code(500)
			                                       .message("Hata oluştu: " + e.getMessage())
			                                       .success(false)
			                                       .data(null)
			                                       .build());
		}
	}
		//Şirket ekleme
	@PostMapping(ADDCOMPANY)
	public ResponseEntity<BaseResponse<Boolean>> addCompany(@RequestBody @Valid CompanyDto companyDto) {
		companyService.addCompany(companyDto);
		
		return ResponseEntity.status(HttpStatus.CREATED)
		                     .body(BaseResponse.<Boolean>builder()
		                                       .code(201)
		                                       .message("Şirket başarıyla oluşturuldu")
		                                       .success(true)
		                                       .data(true)
		                                       .build());
	}
	
	@GetMapping("/v1/api/company/verify-email")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<BaseResponse<String>> verifyEmail(@RequestParam String token,
	                                                        @RequestParam(required = false, defaultValue = "false") boolean redirect) {
		System.out.println("Token received: " + token);
		companyService.verifyEmail(token);
		
		// Eğer yönlendirme isteniyorsa, HTTP 302 Redirect yap
		if (redirect) {
			return ResponseEntity.status(HttpStatus.FOUND)
			                     .header(HttpHeaders.LOCATION, "http://localhost:9090/v1/api/company/company/profile?companyId")
			                     .build();
		}
		
		// JSON yanıt döndür
		return ResponseEntity.ok(BaseResponse.<String>builder()
		                                     .code(200)
		                                     .message("E-posta başarıyla doğrulandı")
		                                     .success(true)
		                                     .data("Hesabınız onaylandı. Artık giriş yapabilirsiniz.")
		                                     .build());
	}
	
	
		
		// 1️⃣ SITE_ADMIN → Tüm şirket profillerine erişebilir
		@GetMapping("/profile/admin")
		@PreAuthorize("hasRole('SITE_ADMIN')")
		public ResponseEntity<BaseResponse<Company>> getAnyCompanyProfile(@RequestParam Long companyId) {
			Optional<Company> company = companyService.findByCompanyId(companyId);
			return company.map(value -> ResponseEntity.ok(
					              BaseResponse.<Company>builder()
					                          .code(200)
					                          .data(value)
					                          .message("Şirket profili başarıyla getirildi")
					                          .success(true)
					                          .build()))
			              .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
			                                             .body(BaseResponse.<Company>builder()
			                                                               .code(ErrorType.DATA_NOT_FOUND.getCode())
			                                                               .message(ErrorType.DATA_NOT_FOUND.getMessage())
			                                                               .success(false)
			                                                               .build()));
		}


//		// 2️⃣ COMPANY_ADMIN → Sadece kendi şirketinin profiline erişebilir
//		@GetMapping("/profile/company-admin")
//		@PreAuthorize("hasRole('COMPANY_ADMIN')")
//		public ResponseEntity<BaseResponse<Company>> getCompanyAdminProfile() {
//			String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//			Optional<Company> company = companyService.findCompanyByUserEmail(currentUsername);
//
//			return company.map(value -> ResponseEntity.ok(
//					              BaseResponse.<Company>builder()
//					                          .code(200)
//					                          .data(value)
//					                          .message("Şirket profili başarıyla getirildi")
//					                          .success(true)
//					                          .build()))
//			              .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//			                                             .body(BaseResponse.<Company>builder()
//			                                                               .code(ErrorType.DATA_NOT_FOUND.getCode())
//			                                                               .message("Şirket bulunamadı veya yetkiniz yok.")
//			                                                               .success(false)
//			                                                               .build()));
//		}

//		// 3️⃣ EMPLOYEE → Sadece çalıştığı şirketin profiline erişebilir
//		@GetMapping("/profile/employee")
//		@PreAuthorize("hasRole('EMPLOYEE')")
//		public ResponseEntity<BaseResponse<Company>> getEmployeeProfile() {
//			String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//			Optional<Company> company = companyService.findCompanyByEmployeeEmail(currentUsername);
//
//			return company.map(value -> ResponseEntity.ok(
//					              BaseResponse.<Company>builder()
//					                          .code(200)
//					                          .data(value)
//					                          .message("Şirket profili başarıyla getirildi")
//					                          .success(true)
//					                          .build()))
//			              .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
//			                                             .body(BaseResponse.<Company>builder()
//			                                                               .code(ErrorType.DATA_NOT_FOUND.getCode())
//			                                                               .message("Çalıştığınız şirket bulunamadı veya yetkiniz yok.")
//			                                                               .success(false)
//			                                                               .build()));
//		}
//	}

	
	
}