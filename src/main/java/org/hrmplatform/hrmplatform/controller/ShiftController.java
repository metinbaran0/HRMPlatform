package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.dto.request.CreateShiftRequest;
import org.hrmplatform.hrmplatform.dto.request.ShiftDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.hrmplatform.hrmplatform.enums.ShiftType;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.mapper.ShiftMapper;

import org.hrmplatform.hrmplatform.service.AuthService;


import org.hrmplatform.hrmplatform.service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hrmplatform.hrmplatform.constant.EndPoints.COMPANY;
import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(SHIFT)
@RequiredArgsConstructor
@CrossOrigin("*")
@PreAuthorize("isAuthenticated()")
@Slf4j
public class ShiftController {
    private final ShiftService shiftService;
    private final ShiftMapper shiftMapper;

    private final AuthService authService;




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
    public ResponseEntity<BaseResponse<List<Shift>>> getAllShifts(
            @RequestHeader("Authorization") String token) { // Token'ı header'dan alıyoruz
        try {
            // ShiftService üzerinden vardiyaları getiriyoruz
            List<Shift> shifts = shiftService.getAllShifts(token);

            // Başarılı yanıt dönüyoruz
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Shifts retrieved successfully",
                    200,
                    shifts));
        } catch (IllegalArgumentException ex) {
            // Geçersiz token veya companyId hatası
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(
                            false,
                            ex.getMessage(),
                            400,
                            null));
        } catch (Exception ex) {
            // Diğer hatalar için 500 Internal Server Error dönüyoruz
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(
                            false,
                            "An unexpected error occurred: " + ex.getMessage(),
                            500,
                            null));
        }
    }


    // Vardiya silme (soft delete)
    @DeleteMapping(DELETE_SHIFT)
    public ResponseEntity<BaseResponse<Void>> deleteShift(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        Long companyId = authService.getCompanyIdFromToken(token); // Token'dan companyId al

        boolean isDeleted = shiftService.deleteShift(id, companyId); // Company ID'yi de gönder

        if (isDeleted) {
            // Başarılı işlem, 200 OK dönüyoruz ve mesaj içeriyor
            BaseResponse<Void> successResponse = BaseResponse.<Void>builder()
                    .code(200)
                    .data(null)
                    .success(true)
                    .message("Vardiya başarıyla silindi.")
                    .build();
            return ResponseEntity.ok(successResponse);
        }

        // Vardiya bulunamadı veya yetkisiz erişim durumunda 404 döndürüyoruz
        BaseResponse<Void> errorResponse = BaseResponse.<Void>builder()
                .code(404)
                .data(null)
                .success(false)
                .message("Vardiya bulunamadı veya yetkisiz erişim.")
                .build();
        return ResponseEntity.status(404).body(errorResponse); // 404, bulunamadı hatası
    }


    // Silinmemiş tüm vardiyaları getir
    @GetMapping(ACTIVE_SHIFT)
    public ResponseEntity<BaseResponse<List<Shift>>> getAllActiveShifts(
            @RequestHeader("Authorization") String token) {

        List<Shift> shifts = shiftService.getAllActiveShifts(token); // Token üzerinden companyId alınıyor

        BaseResponse<List<Shift>> response = BaseResponse.<List<Shift>>builder()
                .code(200)
                .data(shifts)
                .success(true)
                .message("Şirketin silinmemiş vardiyaları başarıyla alındı.")
                .build();
        return ResponseEntity.ok(response);
    }

    //vardiya güncelleme
    @PutMapping(UPDATE_SHIFT)
    public ResponseEntity<BaseResponse<ShiftDto>> updateShift(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody ShiftDto request) {
        try {
            // ShiftService üzerinden vardiyayı güncelliyoruz
            Shift updatedShift = shiftService.updateShift(token, id, request);

            // DTO'ya dönüştürerek döndürüyoruz
            ShiftDto shiftDto = shiftMapper.toShiftDTO(updatedShift);

            return ResponseEntity.ok(BaseResponse.<ShiftDto>builder()
                    .code(HttpStatus.OK.value())
                    .data(shiftDto)
                    .success(true)
                    .message("Vardiya başarıyla güncellendi.")
                    .build());

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<ShiftDto>builder()
                            .code(HttpStatus.NOT_FOUND.value())
                            .data(null)
                            .success(false)
                            .message(ex.getMessage())
                            .build());

        } catch (Exception ex) {
            log.error("Vardiya güncellenirken hata oluştu: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<ShiftDto>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .data(null)
                            .success(false)
                            .message("Beklenmeyen bir hata oluştu.")
                            .build());
        }
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



    // Vardiya dağılımını almak için endpoint
    @GetMapping(DISTRIBUTION)
    public ResponseEntity<Map<Long, List<Shift>>> getShiftDistribution() {
        Map<Long, List<Shift>> distribution = shiftService.getShiftDistribution();
        return ResponseEntity.ok(distribution);
    }

    @GetMapping(GET_ALL_SHIFT_TYPE)
    public ResponseEntity<BaseResponse<List<String>>> getShiftTypes() {
        List<String> shiftTypes = Arrays.stream(ShiftType.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        BaseResponse<List<String>> response = BaseResponse.<List<String>>builder()
                .code(200)
                .data(shiftTypes)
                .success(true)
                .message("Vardiya tipleri başarıyla alındı.")
                .build();

        return ResponseEntity.ok(response);
    }





}
