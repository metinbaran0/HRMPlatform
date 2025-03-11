package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateEmployeeShiftRequest;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.EmployeeShift;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.service.EmployeeShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;


@RestController
@RequestMapping(EMPLOYEE_SHIFT)
@RequiredArgsConstructor
@CrossOrigin("*")
@PreAuthorize("isAuthenticated()")
public class EmployeeShiftController {
    public final EmployeeShiftService employeeShiftService;

    // Çalışan vardiyası oluşturma
    @PostMapping(CREATE_EMPLOYEE_SHIFT)
    public ResponseEntity<BaseResponse<EmployeeShift>> createEmployeeShift(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateEmployeeShiftRequest request) {
        try {
            EmployeeShift employeeShift = employeeShiftService.createEmployeeShift(token, request, request.employeeId(), request.shiftId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.<EmployeeShift>builder()
                            .code(201)
                            .message("Çalışan vardiyası başarıyla oluşturuldu.")
                            .success(true)
                            .data(employeeShift)
                            .build());
        } catch (HRMPlatformException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(BaseResponse.<EmployeeShift>builder()
                            .code(e.hashCode())
                            .message(e.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<EmployeeShift>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + e.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }

    // Bütün çalışan vardiyalarını getirme
    @GetMapping(ALL_EMPLOYEE_SHIFTS)
    public ResponseEntity<BaseResponse<List<EmployeeShift>>> getAllEmployeeShifts(
            @RequestHeader("Authorization") String token) {
        try {
            // Pass token to service layer to get employee shifts for the company
            List<EmployeeShift> employeeShifts = employeeShiftService.getAllEmployeeShifts(token);
            return ResponseEntity.ok(
                    BaseResponse.<List<EmployeeShift>>builder()
                            .code(200)
                            .message("Tüm çalışan vardiyaları başarıyla getirildi.")
                            .success(true)
                            .data(employeeShifts)
                            .build()
            );
        } catch (HRMPlatformException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(BaseResponse.<List<EmployeeShift>>builder()
                            .code(403)
                            .message("Erişim reddedildi: " + e.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<List<EmployeeShift>>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + e.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }


    // Çalışan ID'ye göre vardiyaları getirme
    @GetMapping(GET_EMPLOYEE_SHIFTS_BY_EMPLOYEE_ID)
    public ResponseEntity<BaseResponse<List<EmployeeShift>>> getEmployeeShiftsByEmployeeId(
            @RequestHeader("Authorization") String token,
            @PathVariable Long employeeId) {
        try {
            List<EmployeeShift> employeeShifts = employeeShiftService.getEmployeeShiftsByEmployeeId(token, employeeId);
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Çalışanın vardiyaları başarıyla getirildi.",
                    200,
                    employeeShifts));
        } catch (HRMPlatformException ex) {
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
                            "Bir hata oluştu: " + ex.getMessage(),
                            500,
                            null));
        }
    }

    //çalışan vardiyası güncelleme
    @PutMapping(UPDATE_EMPLOYEE_SHIFT)
    public ResponseEntity<BaseResponse<EmployeeShift>> updateEmployeeShift(
            @RequestHeader("Authorization") String token,
            @PathVariable Long employeeShiftId,
            @RequestBody CreateEmployeeShiftRequest request) {
        try {
            EmployeeShift updatedShift = employeeShiftService.updateEmployeeShift(token, employeeShiftId, request);

            return ResponseEntity.ok(BaseResponse.<EmployeeShift>builder()
                    .code(200)
                    .message("Çalışan vardiyası başarıyla güncellendi.")
                    .success(true)
                    .data(updatedShift)
                    .build());

        } catch (HRMPlatformException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.<EmployeeShift>builder()
                            .code(e.hashCode())
                            .message(e.getMessage())
                            .success(false)
                            .data(null)
                            .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<EmployeeShift>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + e.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }




    // Çalışan vardiyasını soft-delete
    @DeleteMapping(DELETE_EMPLOYEE_SHIFT)
    public ResponseEntity<BaseResponse<Void>> deleteEmployeeShift(@PathVariable Long employeeShiftId) {
        try {
            employeeShiftService.softDeleteEmployeeShift(employeeShiftId);  // Servis metodunu çağırıyoruz
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Çalışan vardiyası başarıyla silindi (soft delete).",
                    200,
                    null));
        } catch (HRMPlatformException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(
                            false,
                            "Veri bulunamadı: " + ex.getMessage(),
                            404,
                            null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(
                            false,
                            "Bir hata oluştu: " + ex.getMessage(),
                            500,
                            null));
        }
    }


    @GetMapping(FILTERDATE)
    public ResponseEntity<BaseResponse<List<EmployeeShift>>> getEmployeeShiftsByDate(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        try {
            List<EmployeeShift> employeeShifts = employeeShiftService.getEmployeeShiftsByDateRange(startDate, endDate);
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Vardiya tarih aralığı başarıyla getirildi.",
                    200,
                    employeeShifts));
        } catch (HRMPlatformException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(
                            false,
                            "Veri bulunamadı: " + ex.getMessage(),
                            404,
                            null));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(
                            false,
                            "Bir hata oluştu: " + ex.getMessage(),
                            500,
                            null));
        }
    }







}
