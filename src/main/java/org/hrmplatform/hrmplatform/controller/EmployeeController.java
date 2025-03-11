package org.hrmplatform.hrmplatform.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.EmployeeRequestDto;
import org.hrmplatform.hrmplatform.dto.request.EmployeeUpdateDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.dto.response.EmployeeResponseDto;
import org.hrmplatform.hrmplatform.dto.response.TokenValidationResult;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.exception.EmployeeNotFoundException;
import org.hrmplatform.hrmplatform.exception.UnauthorizedException;
import org.hrmplatform.hrmplatform.service.AuthService;
import org.hrmplatform.hrmplatform.service.EmployeeService;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.hrmplatform.hrmplatform.view.VwEmployeeResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(EMPLOYEE)
@AllArgsConstructor
@CrossOrigin("*")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final JwtManager jwtManager;
    private final AuthService authService;
    /**
     * Tüm çalışanları getirir. (Sadece ADMIN)
     */
    @GetMapping(GET_ALL_EMPLOYEES)
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<BaseResponse<Page<Employee>>> getAllEmployees(
            @RequestHeader("Authorization") String token, // Token'ı header'dan al
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Token'ı doğrula ve companyId bilgisini al
        Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token.replace("Bearer ", ""));
        if (tokenValidationResult.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, "Geçersiz token", 401, null));
        }

        Long companyId = tokenValidationResult.get().companyId();

        // Eğer companyId null ise, bu kullanıcının bir şirketi yok demektir
        if (companyId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, "Bu işlemi gerçekleştirmek için bir şirkete ait olmalısınız", 403, null));
        }

        // Şirkete ait çalışanları getir
        Page<Employee> employees = employeeService.getAllEmployeesByCompanyId(companyId, page, size);

        if (employees.isEmpty()) {
            return ResponseEntity.ok(new BaseResponse<>(false, "No employees found for company ID: " + companyId, 404, employees));
        }

        return ResponseEntity.ok(new BaseResponse<>(true, "Employees retrieved successfully", 200, employees));
    }


    /**
     * Yeni bir çalışan ekler. (Sadece ADMIN)
     */
    @PostMapping(CREATE_EMPLOYEE)
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<BaseResponse<EmployeeResponseDto>> createEmployee(
            @RequestHeader("Authorization") String token, // Token'ı header'dan al
            @RequestBody @Valid EmployeeRequestDto dto) {

        try {
            // EmployeeService'deki createEmployee metodunu çağır
            EmployeeResponseDto createdEmployee = employeeService.createEmployee(token, dto);

            // Başarılı yanıt dön
            return ResponseEntity.ok(new BaseResponse<>(
                    true,
                    "Employee created successfully",
                    201,
                    createdEmployee));
        } catch (IllegalArgumentException ex) {
            // Hata durumunda 400 Bad Request dön
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(
                            false,
                            ex.getMessage(),
                            400,
                            null));
        } catch (Exception ex) {
            // Diğer hatalar için 500 Internal Server Error dön
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(
                            false,
                            "An unexpected error occurred: " + ex.getMessage(),
                            500,
                            null));
        }
    }
    
    
    /**
     * Var olan bir çalışanı günceller. (Sadece ADMIN)
     */
    @PutMapping(UPDATE_EMPLOYEE)
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<BaseResponse<EmployeeResponseDto>> updateEmployee(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody @Valid EmployeeUpdateDto employeeDetails) {
        try {
            EmployeeResponseDto updatedEmployee = employeeService.updateEmployee(token, id, employeeDetails);
            return ResponseEntity.ok(new BaseResponse<>(true, "Çalışan başarıyla güncellendi", 200, updatedEmployee));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, ex.getMessage(), 400, null));
        } catch (EmployeeNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, ex.getMessage(), 404, null));
        }
    }
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    @GetMapping(GET_EMPLOYEE_BY_ID)
    public ResponseEntity<BaseResponse<EmployeeResponseDto>> getEmployeeById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        try {
            EmployeeResponseDto employee = employeeService.getEmployeeById(token, id);
            return ResponseEntity.ok(new BaseResponse<>(true, "Çalışan bilgileri başarıyla getirildi", 200, employee));
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(false, ex.getMessage(), 401, null));
        } catch (EmployeeNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, ex.getMessage(), 404, null));
        }
    }
    
    
    /**
     * Çalışanı siler. (Sadece ADMIN)
     */
    @DeleteMapping(DELETE_EMPLOYEE)
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            // "Bearer " kısmını kaldır
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            employeeService.deleteEmployee(id, token);
            return ResponseEntity.ok("Employee deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    /**
     * Çalışanın aktif/pasif durumunu değiştirir. (Sadece ADMIN)
     */
    @PutMapping(CHANGE_EMPLOYEE_STATUS)
    @PreAuthorize("hasAuthority('COMPANY_ADMIN')")
    public ResponseEntity<Void> changeEmployeeStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            // "Bearer " kısmını kaldır
            
            
            
            
            
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            employeeService.changeEmployeeStatus(id, token);
            return ResponseEntity.ok().build(); // 200 OK döner
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized döner
        }
    }
    
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('COMPANY_ADMIN')")
    @GetMapping("/searchEmployeeByName")
    public ResponseEntity<BaseResponse<List<VwEmployeeResponse>>> searchEmployeeByName(
            @RequestHeader("Authorization") String token,
            @RequestParam String name) {
        
        authService.getCompanyIdFromToken(token); // Yetkilendirme ve şirket doğrulaması
        
        List<VwEmployeeResponse> employees = employeeService.searchEmployeeByName(name);
        
        if (employees.isEmpty()) {
            return ResponseEntity.ok(BaseResponse.<List<VwEmployeeResponse>>builder()
                                                 .code(404)
                                                 .message("Hiçbir çalışan bulunamadı")
                                                 .success(false)
                                                 .data(employees)
                                                 .build());
        }
        
        return ResponseEntity.ok(BaseResponse.<List<VwEmployeeResponse>>builder()
                                             .code(200)
                                             .message("Çalışanlar başarıyla getirildi")
                                             .success(true)
                                             .data(employees)
                                             .build());
    }



}