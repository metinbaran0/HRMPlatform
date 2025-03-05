package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.BreakRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Break;
import org.hrmplatform.hrmplatform.exception.ErrorType;
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
    public ResponseEntity<BaseResponse<Break>> createBreak(@RequestBody BreakRequestDto breakRequest) {
        Break createdBreak = breakService.createBreak(breakRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.<Break>builder()
                        .code(201)
                        .message("Mola başarıyla oluşturuldu")
                        .success(true)
                        .data(createdBreak)
                        .build());
    }
    //bütün molaları getirme
    @GetMapping(ALL_BREAK)
    public ResponseEntity<BaseResponse<List<Break>>> getAllBreaks() {
        List<Break> breaks = breakService.getAllBreaks();

        return ResponseEntity.ok(
                BaseResponse.<List<Break>>builder()
                        .code(200)
                        .data(breaks)
                        .message("Mola başarıyla getirildi")
                        .success(true)
                        .build()
        );
    }
    //id'e göre mola getirme
    @GetMapping(GET_BREAK_BYID)
    public ResponseEntity<BaseResponse<Break>> getBreakById(@PathVariable Long breakId) {
        Optional<Break> optionalBreak = breakService.getBreakById(breakId);
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
    }
    //vardiya Id'e göre mola getirme
    @GetMapping(GET_BREAK_BYSHIFT)
    public ResponseEntity<BaseResponse<List<Break>>> getBreaksByShift(@PathVariable Long shiftId) {
        return buildBreakResponse(breakService.getBreaksByShiftId(shiftId), "Vardiyaya ait molalar getirildi");
    }
    //şirket Id'e göre mola getirme
    @GetMapping(GET_BREAK_BYCOMPANY)
    public ResponseEntity<BaseResponse<List<Break>>> getBreaksByCompany(@PathVariable Long companyId) {
        return buildBreakResponse(breakService.getBreaksByCompany(companyId), "Şirkete ait molalar getirildi");
    }

    //mola güncelleme
    @PutMapping(UPDATE_BREAK)
    public ResponseEntity<BaseResponse<Break>> updateBreak(@PathVariable Long breakId, @RequestBody BreakRequestDto breakRequest) {
        Optional<Break> optionalBreak = breakService.getBreakById(breakId);

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
    }
    //mola soft-delete
    @DeleteMapping(DELETE_BREAK)
    public ResponseEntity<BaseResponse<Break>> softDeleteBreak(@PathVariable Long breakId) {
        Optional<Break> optionalBreak = breakService.getBreakById(breakId);

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


}
