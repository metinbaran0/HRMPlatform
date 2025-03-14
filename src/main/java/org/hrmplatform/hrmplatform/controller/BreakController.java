package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.BreakRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Break;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.service.AuthService;
import org.hrmplatform.hrmplatform.service.BreakService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(BREAK)
@RequiredArgsConstructor
@CrossOrigin("*")
@PreAuthorize("isAuthenticated()")
public class BreakController {
    private final BreakService breakService;


    //Mola oluşturma
    @PostMapping(CREATE_BREAK)
    public ResponseEntity<BaseResponse<Break>> createBreak(
            @RequestHeader("Authorization") String token,
            @RequestBody BreakRequestDto breakRequest) {
        try {
            Break createdBreak = breakService.createBreak(token, breakRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(BaseResponse.<Break>builder()
                            .code(201)
                            .message("Mola başarıyla oluşturuldu")
                            .success(true)
                            .data(createdBreak)
                            .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.<Break>builder()
                            .code(400)
                            .message(ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<Break>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }
    //bütün molaları getirme
    @GetMapping(ALL_BREAK)
    public ResponseEntity<BaseResponse<List<Break>>> getAllBreaks(
            @RequestHeader("Authorization") String token) {
        try {
            List<Break> breaks = breakService.getAllBreaks(token);

            return ResponseEntity.ok(
                    BaseResponse.<List<Break>>builder()
                            .code(200)
                            .data(breaks)
                            .message("Mola başarıyla getirildi")
                            .success(true)
                            .build()
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<List<Break>>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }
    //id'e göre mola getirme
    @GetMapping(GET_BREAK_BYID)
    public ResponseEntity<BaseResponse<Break>> getBreakById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long breakId) {
        try {
            Optional<Break> optionalBreak = breakService.getBreakById(token, breakId);
            return optionalBreak.map(breakItem -> ResponseEntity.ok(
                            BaseResponse.<Break>builder()
                                    .code(200)
                                    .data(breakItem)
                                    .message("ID'ye ait mola başarıyla getirildi")
                                    .success(true)
                                    .build()))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(BaseResponse.<Break>builder()
                                    .code(ErrorType.DATA_NOT_FOUND.getCode())
                                    .message(ErrorType.DATA_NOT_FOUND.getMessage())
                                    .success(false)
                                    .build()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<Break>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }
    //vardiya Id'e göre mola getirme
    @GetMapping(GET_BREAK_BYSHIFT)
    public ResponseEntity<BaseResponse<List<Break>>> getBreaksByShift(
            @RequestHeader("Authorization") String token,
            @PathVariable Long shiftId) {
        try {
            List<Break> breaks = breakService.getBreaksByShiftId(token, shiftId);
            return buildBreakResponse(breaks, "Vardiyaya ait molalar getirildi");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<List<Break>>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }
    //şirket Id'e göre mola getirme
    @GetMapping(GET_BREAK_BYCOMPANY)
    public ResponseEntity<BaseResponse<List<Break>>> getBreaksByCompany(
            @RequestHeader("Authorization") String token,
            @PathVariable Long companyId) {
        try {
            List<Break> breaks = breakService.getBreaksByCompany(token, companyId);
            return buildBreakResponse(breaks, "Şirkete ait molalar getirildi");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<List<Break>>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }

    //mola güncelleme
    @PutMapping(UPDATE_BREAK)
    public ResponseEntity<BaseResponse<Break>> updateBreak(
            @RequestHeader("Authorization") String token,
            @PathVariable Long breakId,
            @RequestBody BreakRequestDto breakRequest) {
        try {
            Optional<Break> optionalBreak = breakService.getBreakById(token, breakId);

            if (optionalBreak.isPresent()) {
                Break updatedBreak = breakService.updateBreak(breakId, breakRequest);
                return ResponseEntity.ok(
                        BaseResponse.<Break>builder()
                                .code(200)
                                .message("Mola başarıyla güncellendi")
                                .success(true)
                                .data(updatedBreak)
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.<Break>builder()
                                .code(ErrorType.DATA_NOT_FOUND.getCode())
                                .message("Mola bulunamadı")
                                .success(false)
                                .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<Break>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }

    //mola soft-delete
    @DeleteMapping(DELETE_BREAK)
    public ResponseEntity<BaseResponse<Break>> softDeleteBreak(
            @RequestHeader("Authorization") String token,
            @PathVariable Long breakId) {
        try {
            Optional<Break> optionalBreak = breakService.getBreakById(token, breakId);

            if (optionalBreak.isPresent()) {
                breakService.softDeleteBreak(breakId);
                return ResponseEntity.ok(
                        BaseResponse.<Break>builder()
                                .code(200)
                                .message("Mola başarıyla silindi")
                                .success(true)
                                .data(optionalBreak.get())
                                .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.<Break>builder()
                                .code(ErrorType.DATA_NOT_FOUND.getCode())
                                .message(ErrorType.DATA_NOT_FOUND.getMessage())
                                .success(false)
                                .build());
            }
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(BaseResponse.<Break>builder()
                            .code(500)
                            .message("Beklenmeyen bir hata oluştu: " + ex.getMessage())
                            .success(false)
                            .data(null)
                            .build());
        }
    }

    // Kullanıcının şirketine ait silinmemiş molaları getirme
    @GetMapping(ALL_ACTIVE_BREAKS)
    public ResponseEntity<BaseResponse<List<Break>>> getActiveBreaks(@RequestHeader("Authorization") String token) {
        try {
            List<Break> activeBreaks = breakService.getActiveBreaksForCompany(token); // Token ile filtreleme
            return ResponseEntity.ok(
                    BaseResponse.<List<Break>>builder()
                            .code(200)
                            .data(activeBreaks)
                            .message("Silinmemiş molalar başarıyla getirildi")
                            .success(true)
                            .build()
            );
        } catch (RuntimeException e) {
            // Eğer liste boşsa veya başka bir hata oluşursa
            return ResponseEntity.ok(
                    BaseResponse.<List<Break>>builder()
                            .code(200)
                            .data(List.of()) // Boş liste döndür
                            .message("Boş mola listesi")
                            .success(true)
                            .build()
            );
        }
    }


    private ResponseEntity<BaseResponse<List<Break>>> buildBreakResponse(List<Break> breaks, String successMessage) {
        if (!breaks.isEmpty()) {
            return ResponseEntity.ok(
                    BaseResponse.<List<Break>>builder()
                            .code(200)
                            .data(breaks)
                            .message(successMessage)
                            .success(true)
                            .build()
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(BaseResponse.<List<Break>>builder()
                            .code(ErrorType.DATA_NOT_FOUND.getCode())
                            .message(ErrorType.DATA_NOT_FOUND.getMessage())
                            .success(false)
                            .data(Collections.emptyList())
                            .build());
        }
    }

    @ExceptionHandler(HRMPlatformException.class)
    public ResponseEntity<BaseResponse<?>> handleHRMPlatformException(HRMPlatformException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(ex.getMessage()))
                .body(BaseResponse.builder()
                        .code(ex.getErrorType().getCode())
                        .message(ex.getMessage())
                        .success(false)
                        .data(null)
                        .build());
    }


}
