package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateShiftRequest;
import org.hrmplatform.hrmplatform.dto.request.ShiftDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.hrmplatform.hrmplatform.enums.ShiftType;
import org.hrmplatform.hrmplatform.service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hrmplatform.hrmplatform.constant.EndPoints.COMPANY;
import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(SHIFT)
@RequiredArgsConstructor
@CrossOrigin("*")
@PreAuthorize("isAuthenticated()")
public class ShiftController {
    private final ShiftService shiftService;


    @PostMapping(CREATE_SHIFT)
    public ResponseEntity<BaseResponse<ShiftDto>> createShift(
            @RequestHeader("Authorization") String token,
            @RequestBody ShiftDto shiftDto) {
        try {
            ShiftDto createdShift = shiftService.createShift(token, shiftDto);
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Shift created successfully",
                    201,
                    createdShift));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(
                            false,
                            ex.getMessage(),
                            400,
                            null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(
                            false,
                            "An unexpected error occurred: " + ex.getMessage(),
                            500,
                            null));
        }
    }
    /**
     *@PostMapping(CREATE_SHIFT)
     * public ResponseEntity<BaseResponse<ShiftDto>> createShift(@RequestBody ShiftDto shiftDto, @AuthenticationPrincipal UserDetails userDetails) {
     *     Long companyId = userDetails.getCompanyId();  // Oturumdan veya kullanıcıdan companyId'yi alıyorsunuz
     *     ShiftDto createdShift = shiftService.createShift(companyId, shiftDto.shiftName(),
     *             shiftDto.startTime(), shiftDto.endTime(), shiftDto.shiftType());
     *
     *     BaseResponse<ShiftDto> response = BaseResponse.<ShiftDto>builder()
     *             .success(true)
     *             .message("Shift created successfully")
     *             .code(HttpStatus.CREATED.value())
     *             .data(createdShift)
     *             .build();
     *
     *     return ResponseEntity.status(HttpStatus.CREATED).body(response);
     * }
     *
     */

    //tüm vardiyalrı getirme
    @GetMapping(GETALL_SHIFT)
    public ResponseEntity<BaseResponse<List<Shift>>> getAllShifts() {
        List<Shift> shifts = shiftService.getAllShifts();
        BaseResponse<List<Shift>> response = BaseResponse.<List<Shift>>builder()
                .code(200)
                .data(shifts)
                .success(true)
                .message("Vardiyalar başarıyla getirildi.")
                .build();
        return ResponseEntity.ok(response);
    }

    // Id'ye göre vardiya getirme
    @GetMapping(GETSHIF_BYID)
    public ResponseEntity<BaseResponse<Shift>> getShiftById(@PathVariable Long id) {
        Shift shift = shiftService.getShiftById(id);
        if (shift != null) {
            BaseResponse<Shift> response = BaseResponse.<Shift>builder()
                    .code(200)
                    .data(shift)
                    .success(true)
                    .message("Vardiya başarıyla bulundu.")
                    .build();
            return ResponseEntity.ok(response);
        }
        BaseResponse<Shift> response = BaseResponse.<Shift>builder()
                .code(404)
                .data(null)
                .success(false)
                .message("Vardiya bulunamadı.")
                .build();
        return ResponseEntity.status(404).body(response);
    }

    //belirli bir şirkete ait vardiyaları getirme
    @GetMapping(GETSHIFTBY_COMPANYID)
    public ResponseEntity<BaseResponse<List<Shift>>> getShiftsByCompanyId(@PathVariable Long companyId) {
        List<Shift> shifts = shiftService.getShiftsByCompanyId(companyId);
        if (shifts != null && !shifts.isEmpty()) {
            BaseResponse<List<Shift>> response = BaseResponse.<List<Shift>>builder()
                    .code(200)
                    .data(shifts)
                    .success(true)
                    .message("Şirketin vardiyaları başarıyla getirildi.")
                    .build();
            return ResponseEntity.ok(response);
        }
        BaseResponse<List<Shift>> response = BaseResponse.<List<Shift>>builder()
                .code(404)
                .data(null)
                .success(false)
                .message("Şirkete ait vardiya bulunamadı.")
                .build();
        return ResponseEntity.status(404).body(response);
    }

    //vardiya silme(soft delete)
    @DeleteMapping(DELETE_SHIFT)
    public ResponseEntity<BaseResponse<Void>> deleteShift(@PathVariable Long id) {
        boolean isDeleted = shiftService.deleteShift(id);
        if (isDeleted) {
            BaseResponse<Void> response = BaseResponse.<Void>builder()
                    .code(200)
                    .data(null)
                    .success(true)
                    .message("Vardiya başarıyla silindi (soft delete).")
                    .build();
            return ResponseEntity.noContent().build(); // No content döner çünkü aslında veriyi silmedik
        }
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .code(404)
                .data(null)
                .success(false)
                .message("Vardiya bulunamadı.")
                .build();
        return ResponseEntity.status(404).body(response);
    }

    // Silinmemiş tüm vardiyaları getir
    @GetMapping(ACTIVE_SHIFT)
    public ResponseEntity<BaseResponse<List<Shift>>> getAllActiveShifts() {
        List<Shift> shifts = shiftService.getAllActiveShifts(); // Servisten silinmemiş vardiyaları alıyoruz
        BaseResponse<List<Shift>> response = BaseResponse.<List<Shift>>builder()
                .code(200)
                .data(shifts)
                .success(true)
                .message("Silinmemiş vardiyalar başarıyla alındı.")
                .build();
        return ResponseEntity.ok(response);
    }

    //vardiya güncelleme
    @PutMapping(UPDATE_SHIFT)
    public ResponseEntity<BaseResponse<Shift>> updateShift(@PathVariable Long id, @RequestBody CreateShiftRequest request) {
        Shift updatedShift = shiftService.updateShift(id, request);
        if (updatedShift != null) {
            return ResponseEntity.ok(BaseResponse.<Shift>builder()
                    .code(200)
                    .data(updatedShift)
                    .success(true)
                    .message("Vardiya başarıyla güncellendi.")
                    .build());
        }
        return ResponseEntity.status(404).body(BaseResponse.<Shift>builder()
                .code(404)
                .data(null)
                .success(false)
                .message("Vardiya bulunamadı.")
                .build());
    }

    @GetMapping(SHIFTTYPE)
    public ResponseEntity<BaseResponse<List<Shift>>> getShiftsByShiftType(@PathVariable ShiftType shiftType) {
        try {
            // Vardiya türüne göre vardiya listesi alınıyor
            List<Shift> shifts = shiftService.getShiftsByShiftType(shiftType);

            // Eğer vardiya listesi boşsa, uygun bir mesaj ile döner
            if (shifts.isEmpty()) {
                return ResponseEntity.ok(
                        BaseResponse.<List<Shift>>builder()
                                .code(204)
                                .message("Belirtilen vardiya türüne ait herhangi bir vardiya bulunmamaktadır.")
                                .success(true)
                                .data(shifts)
                                .build()
                );
            }

            // Başarılı sonuç dönülür
            return ResponseEntity.ok(
                    BaseResponse.<List<Shift>>builder()
                            .code(200)
                            .message("Vardiya türüne ait vardiyalar başarıyla getirildi.")
                            .success(true)
                            .data(shifts)
                            .build()
            );
        } catch (Exception e) {
            // Hata durumunda uygun bir mesaj ile hata kodu dönülür
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<List<Shift>>builder()
                            .code(500)
                            .message("Vardiya listesi alınırken bir hata oluştu: " + e.getMessage())
                            .success(false)
                            .data(Collections.emptyList()) // Boş liste gönderilir
                            .build()
                    );
        }
    }

    //belirli bir tarihe aralığına ait vardiyaları getirme
    @GetMapping(DATE_SHIFT)
    public ResponseEntity<BaseResponse<List<Shift>>> getShiftsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<Shift> shifts = shiftService.getShiftsByDateRange(startDate, endDate);
        return ResponseEntity.ok(
                BaseResponse.<List<Shift>>builder()
                        .code(200)
                        .message("Tarih aralığındaki vardiyalar başarıyla getirildi.")
                        .success(true)
                        .data(shifts)
                        .build()
        );
    }

    // Vardiya çakışmasını kontrol eden endpoint
    @GetMapping("/employee/{employeeId}/shift-conflict")
    public ResponseEntity<BaseResponse<Boolean>> checkShiftConflict(@PathVariable Long employeeId, @RequestParam LocalDate date) {
        boolean hasConflict = shiftService.checkShiftConflict(employeeId, date);
        return ResponseEntity.ok(
                BaseResponse.<Boolean>builder()
                        .code(200)
                        .message("true")
                        .success(true)
                        .data(hasConflict)
                        .build());
    }

    // Vardiya dağılımını almak için endpoint
    @GetMapping(DISTRIBUTION)
    public ResponseEntity<Map<Long, List<Shift>>> getShiftDistribution() {
        Map<Long, List<Shift>> distribution = shiftService.getShiftDistribution();
        return ResponseEntity.ok(distribution);
    }






}
