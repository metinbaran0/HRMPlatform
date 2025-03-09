package org.hrmplatform.hrmplatform.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.EmployeeRequestDto;
import org.hrmplatform.hrmplatform.dto.request.EmployeeUpdateDto;
import org.hrmplatform.hrmplatform.dto.response.EmployeeResponseDto;
import org.hrmplatform.hrmplatform.dto.response.TokenValidationResult;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.exception.*;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.mapper.EmployeeMapper;
import org.hrmplatform.hrmplatform.repository.EmployeeRepository;
import org.hrmplatform.hrmplatform.util.JwtManager;
import org.hrmplatform.hrmplatform.util.PaginationUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    @Lazy
    private final CompanyService companyService;
    private final EmployeeMapper employeeMapper;
    private final JwtManager jwtManager;



    public Page<Employee> getAllEmployeesByCompanyId(Long companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findByCompanyId(companyId, pageable);
    }

    public EmployeeResponseDto createEmployee(
            @RequestHeader("Authorization") String token,
            @RequestBody @Valid EmployeeRequestDto request) {

        // Token'ı doğrula ve companyId bilgisini al
        Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token.replace("Bearer ", ""));
        if (tokenValidationResult.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz token");
        }

        Long companyId = tokenValidationResult.get().companyId();

        // Eğer companyId null ise, bu kullanıcının bir şirketi yok demektir
        if (companyId == null) {
            throw new IllegalArgumentException("Bu işlemi gerçekleştirmek için bir şirkete ait olmalısınız");
        }

        // Şirketi bul
        Company company = companyService.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        // Employee nesnesini oluştur ve companyId bilgisini ekle
        Employee employee = Employee.builder()
                .companyId(company.getId())
                .name(request.name())
                .surname(request.surname())
                .email(request.email())
                .phone(request.phone())
                .position(request.position())
                .isActive(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Employee'yi kaydet
        Employee savedEmployee = employeeRepository.save(employee);

        // EmployeeResponseDto'yu oluştur ve dön
        return new EmployeeResponseDto(
                savedEmployee.getId(),
                company.getName(), // Şirket adını ekledik
                savedEmployee.getName() + " " + savedEmployee.getSurname(), // fullName = name + surname
                savedEmployee.getEmail(),
                savedEmployee.getPhone(),
                savedEmployee.getPosition(),
                savedEmployee.isActive()
        );
    }

    public void deleteEmployee(Long id, String token) {
        // Token'ı doğrula ve companyId bilgisini al
        Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token);
        if (tokenValidationResult.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz token");
        }

        Long companyId = tokenValidationResult.get().companyId();

        // Eğer companyId null ise, bu kullanıcının bir şirketi yok demektir
        if (companyId == null) {
            throw new IllegalArgumentException("Bu işlemi gerçekleştirmek için bir şirkete ait olmalısınız");
        }

        // Employee'yi bul
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));

        // ADMIN sadece kendi şirketindeki çalışanları silebilir
        if (!employee.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("Unauthorized access: You can only delete employees from your own company");
        }

        // Employee'yi sil
        employeeRepository.delete(employee);
    }

    /**
     * Mevcut bir çalışanı günceller.
     * Çalışan bulunamazsa hata döndürülür.
     */
    public EmployeeResponseDto updateEmployee(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody @Valid EmployeeUpdateDto employeeDetails) {

        // Token'ı doğrula ve companyId bilgisini al
        Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token.replace("Bearer ", ""));
        if (tokenValidationResult.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz token");
        }

        Long companyId = tokenValidationResult.get().companyId();

        // Eğer companyId null ise, bu kullanıcının bir şirketi yok demektir
        if (companyId == null) {
            throw new IllegalArgumentException("Bu işlemi gerçekleştirmek için bir şirkete ait olmalısınız");
        }

        // Şirketi bul
        Company company = companyService.findByCompanyId(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + companyId));

        // Çalışanı bul
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(ErrorType.EMPLOYEE_NOT_FOUND));

        // Çalışanın şirket ID'si ile token'daki şirket ID'si eşleşmiyorsa hata fırlat
        if (!employee.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("Bu çalışanı güncellemek için yetkiniz yok");
        }

        // Mapper kullanarak sadece null olmayan değerleri güncelle
        employeeMapper.Instance.updateEmployeeFromDto(employeeDetails, employee);

        // Güncellenen çalışanı kaydet
        Employee updatedEmployee = employeeRepository.save(employee);

        // EmployeeResponseDto'yu oluştur ve dön
        return new EmployeeResponseDto(
                updatedEmployee.getId(),
                company.getName(), // Şirket adını ekledik
                updatedEmployee.getName() + " " + updatedEmployee.getSurname(), // fullName = name + surname
                updatedEmployee.getEmail(),
                updatedEmployee.getPhone(),
                updatedEmployee.getPosition(),
                updatedEmployee.isActive()
        );
    }

    public EmployeeResponseDto getEmployeeById(String token, Long id) {
        // Token'ı doğrula ve companyId bilgisini al
        Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token.replace("Bearer ", ""));
        if (tokenValidationResult.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz token");
        }

        Long companyId = tokenValidationResult.get().companyId();

        // Eğer companyId null ise, bu kullanıcının bir şirketi yok demektir
        if (companyId == null) {
            throw new IllegalArgumentException("Bu işlemi gerçekleştirmek için bir şirkete ait olmalısınız");
        }

        // Çalışanı bul
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(ErrorType.EMPLOYEE_NOT_FOUND));

        // Çalışanın şirket ID'si ile token'daki şirket ID'si eşleşmiyorsa hata fırlat
        if (!employee.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("Bu çalışanı görüntülemek için yetkiniz yok");
        }

        // EmployeeResponseDto'yu oluştur ve dön
        return new EmployeeResponseDto(
                employee.getId(),
                employee.getName(),
                employee.getSurname(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getPosition(),
                employee.isActive()
        );
    }


    /**
     * Çalışanı veritabanından siler.
     */


    /**
     * Çalışanın aktiflik durumunu değiştirir.
     * Güncelleme yapıldıktan sonra kayıt edilir ve e-posta gönderilir.
     */
    public void changeEmployeeStatus(Long id, String token) {
        // Token'ı doğrula ve companyId bilgisini al
        Optional<TokenValidationResult> tokenValidationResult = jwtManager.validateToken(token);
        if (tokenValidationResult.isEmpty()) {
            throw new IllegalArgumentException("Geçersiz token");
        }

        Long companyId = tokenValidationResult.get().companyId();

        // Eğer companyId null ise, bu kullanıcının bir şirketi yok demektir
        if (companyId == null) {
            throw new IllegalArgumentException("Bu işlemi gerçekleştirmek için bir şirkete ait olmalısınız");
        }

        // Employee'yi bul
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + id));

        // ADMIN sadece kendi şirketindeki çalışanların durumunu değiştirebilir
        if (!employee.getCompanyId().equals(companyId)) {
            throw new IllegalArgumentException("Unauthorized access: You can only change status of employees from your own company");
        }

        // Durumu tersine çevir
        boolean newStatus = !employee.isActive();
        employee.setActive(newStatus);
        employee.setUpdatedAt(LocalDateTime.now());

        // E-posta içeriğini belirle
        String subject = newStatus ? "Hesabınız Aktif Edildi" : "Hesabınız Devre Dışı Bırakıldı";
        String message = newStatus ?
                "Merhaba " + employee.getName() + ",\n\nHesabınız tekrar aktif hale getirildi. Artık platformu kullanabilirsiniz." :
                "Merhaba " + employee.getName() + ",\n\nHesabınız devre dışı bırakıldı. Lütfen yöneticinizle iletişime geçin.";

        // E-posta gönder
        emailService.sendEmail(employee.getEmail(), subject, message);

        // Employee'yi güncelle ve kaydet
        employeeRepository.save(employee);
    }
}
    
    

    

    

    

    

