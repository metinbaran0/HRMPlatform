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
    public ResponseEntity<BaseResponse<EmployeeShift>> createEmployeeShift(@RequestBody CreateEmployeeShiftRequest request) {
        try {
            EmployeeShift employeeShift = employeeShiftService.createEmployeeShift(request, request.employeeId(), request.shiftId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.<EmployeeShift>builder()
                            .code(201)
                            .message("Çalışan vardiyası başarıyla oluşturuldu.")
                            .success(true)
                            .data(employeeShift)
                            .build());
        } catch ( HRMPlatformException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(BaseResponse.<EmployeeShift>builder()
                            .code(409)
                            .message(e.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }

    // Bütün çalışan vardiyalarını getirme
    @GetMapping(ALL_EMPLOYEE_SHIFTS)
    public ResponseEntity<BaseResponse<List<EmployeeShift>>> getAllEmployeeShifts() {
        List<EmployeeShift> employeeShifts = employeeShiftService.getAllEmployeeShifts();
        return ResponseEntity.ok(
                BaseResponse.<List<EmployeeShift>>builder()
                        .code(200)
                        .message("Tüm çalışan vardiyaları başarıyla getirildi.")
                        .success(true)
                        .data(employeeShifts)
                        .build()
        );
    }

    // Çalışan ID'ye göre vardiyaları getirme
    @GetMapping(GET_EMPLOYEE_SHIFTS_BY_EMPLOYEE_ID)
    public ResponseEntity<BaseResponse<List<EmployeeShift>>> getEmployeeShiftsByEmployeeId(@PathVariable Long employeeId) {
        List<EmployeeShift> employeeShifts = employeeShiftService.getEmployeeShiftsByEmployeeId(employeeId);
        return ResponseEntity.ok(
                BaseResponse.<List<EmployeeShift>>builder()
                        .code(200)
                        .message("Çalışanın vardiyaları başarıyla getirildi.")
                        .success(true)
                        .data(employeeShifts)
                        .build()
        );
    }

    @PutMapping(UPDATE_EMPLOYEE_SHIFT)
    public ResponseEntity<BaseResponse<EmployeeShift>> updateEmployeeShift(@PathVariable Long employeeShiftId,
                                                                           @RequestBody CreateEmployeeShiftRequest request) {
        // Service üzerinden çalışan ve vardiya ID'lerini doğrulama
        employeeShiftService.validateEmployeeAndShift(request.employeeId(), request.shiftId());

        EmployeeShift updatedShift = employeeShiftService.updateEmployeeShift(employeeShiftId, request);
        return ResponseEntity.ok(
                BaseResponse.<EmployeeShift>builder()
                        .code(200)
                        .message("Çalışan vardiyası başarıyla güncellendi.")
                        .success(true)
                        .data(updatedShift)
                        .build()
        );
    }


    // Çalışan vardiyasını soft-delete
    @DeleteMapping(DELETE_EMPLOYEE_SHIFT)
    public ResponseEntity<BaseResponse<Void>> deleteEmployeeShift(@PathVariable Long employeeShiftId) {
        employeeShiftService.softDeleteEmployeeShift(employeeShiftId);  // Servis metodunu çağırıyoruz

        return ResponseEntity.ok(
                BaseResponse.<Void>builder()
                        .code(200)
                        .message("Çalışan vardiyası başarıyla silindi (soft delete).")
                        .success(true)
                        .build()
        );
    }

    @GetMapping(FILTERDATE)
    public ResponseEntity<BaseResponse<List<EmployeeShift>>> getEmployeeShiftsByDate(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        List<EmployeeShift> employeeShifts = employeeShiftService.getEmployeeShiftsByDateRange(startDate, endDate);
        return ResponseEntity.ok(
                BaseResponse.<List<EmployeeShift>>builder()
                        .code(200)
                        .message("Vardiya tarih aralığı başarıyla getirildi.")
                        .success(true)
                        .data(employeeShifts)
                        .build()
        );
    }






}
