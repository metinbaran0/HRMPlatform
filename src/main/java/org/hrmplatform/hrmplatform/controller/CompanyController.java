package org.hrmplatform.hrmplatform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.datafaker.providers.base.Bool;
import org.hrmplatform.hrmplatform.dto.request.CompanyDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.enums.SubscriptionPlan;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping(COMPANY)
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

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
                        .build()
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
    @PutMapping(APPROVE+"/{id}")
    public ResponseEntity<BaseResponse<Company>> approveCompany(@PathVariable Long id) {
        Company approvedCompany =companyService.approveCompany(id);

        return ResponseEntity.ok(BaseResponse.<Company>builder()
                .code(200)
                .message("Şirket başvurusu onaylandı")
                .success(true)
                .data(approvedCompany)
                .build());
    }
    //şirket başvurusu reddetme
    @PutMapping(REJECT+"/{id}")
    public ResponseEntity<BaseResponse<Company>> rejectCompany(@PathVariable Long id) {
        Company rejectedCompany =companyService.rejectCompany(id);

        return ResponseEntity.ok(BaseResponse.<Company>builder()
                .code(200)
                .message("Şirket başvurusu reddedildi")
                .success(true)
                .data(rejectedCompany)
                .build());
    }

    //üyelik planı
    public ResponseEntity<BaseResponse<Company>> setSubscriptionPlan(@PathVariable Long id, @RequestParam SubscriptionPlan plan) {
       Company updatedCompany= companyService.setSubscriptionPlan(id,plan);
       return ResponseEntity.ok(BaseResponse.<Company>builder()
               .code(200)
               .message("Şirket üyelik planı güncellendi")
               .success(true)
               .data(updatedCompany)
               .build());
    }

}
