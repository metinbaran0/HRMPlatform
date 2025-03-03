package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateShiftRequest;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.hrmplatform.hrmplatform.service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.COMPANY;
import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(SHIFT)
@RequiredArgsConstructor
@CrossOrigin("*")
@PreAuthorize("isAuthenticated()")
public class ShiftController {
    private final ShiftService shiftService;

    //HATALI KOD SECURITY HATASI ALIYORUM
    @PostMapping(CREATE_SHIFT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Shift>> createShift(@RequestBody CreateShiftRequest request, @RequestParam Long companyId) {
        Shift shift = shiftService.createShift(request, companyId);
        return ResponseEntity.ok(BaseResponse.<Shift>builder()
                .code(200)
                .data(shift)
                .success(true)
                .message("Vardiya başarıyla oluşturuldu.")
                .build());
    }
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

}
