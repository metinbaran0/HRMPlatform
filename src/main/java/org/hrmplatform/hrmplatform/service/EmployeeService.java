package org.hrmplatform.hrmplatform.service;

import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.EmployeeRequestDto;
import org.hrmplatform.hrmplatform.dto.request.EmployeeUpdateDto;
import org.hrmplatform.hrmplatform.dto.response.EmployeeResponseDto;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.exception.EmployeeNotFoundException;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.mapper.EmployeeMapper;
import org.hrmplatform.hrmplatform.repository.EmployeeRepository;
import org.hrmplatform.hrmplatform.util.PaginationUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
   

    /**
     * Tüm çalışanları getirir (sayfalama eklenmiştir).
     * Veritabanını yormamak için sayfalama kullanıyoruz.
     */
    public Page<Employee> getAllEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable);
    }

    public EmployeeResponseDto createEmployee(EmployeeRequestDto request) {
        Company company = companyService.findByCompanyId(request.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + request.companyId()));

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

        Employee savedEmployee = employeeRepository.save(employee);

        // **Manuel Mapping**
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


    /**
     * Mevcut bir çalışanı günceller.
     * Çalışan bulunamazsa hata döndürülür.
     */
    public void updateEmployee(Long id, EmployeeUpdateDto employeeDetails) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(ErrorType.EMPLOYEE_NOT_FOUND));

        // 🔹 Mapper kullanarak sadece null olmayan değerleri güncelle
        employeeMapper.Instance.updateEmployeeFromDto(employeeDetails, employee);

        Employee updatedEmployee = employeeRepository.save(employee);

    }


    /**
     * Çalışanı veritabanından siler.
     */
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElseThrow(() -> new RuntimeException("Employee not found"));
        employeeRepository.delete(employee);
    }

    /**
     * Çalışanın aktiflik durumunu değiştirir.
     * Güncelleme yapıldıktan sonra kayıt edilir ve e-posta gönderilir.
     */
    public Employee changeEmployeeStatus(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        boolean newStatus = !employee.isActive(); // Durumu tersine çevir
        employee.setActive(newStatus);
        employee.setUpdatedAt(LocalDateTime.now());

        // E-posta içeriğini belirle
        String subject = newStatus ? "Hesabınız Aktif Edildi" : "Hesabınız Devre Dışı Bırakıldı";
        String message = newStatus ?
                "Merhaba " + employee.getName() + ",\n\nHesabınız tekrar aktif hale getirildi. Artık platformu kullanabilirsiniz." :
                "Merhaba " + employee.getName() + ",\n\nHesabınız devre dışı bırakıldı. Lütfen yöneticinizle iletişime geçin.";

        // E-posta gönder
        emailService.sendEmail(employee.getEmail(), subject, message);

        return employeeRepository.save(employee);
    }
    
    
              //  METIN
    
    
    // companyId'ye göre çalışanları bulma
    public Employee findEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                                 .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı"));
    }
    
    // companyId ile şirkete ait çalışanları pasif hale getirme
    public void deactivateEmployeesByCompanyId(Long companyId) {
        // Şirketin çalışanlarını bul ve pasif hale getir
        List<Employee> employees = employeeRepository.findByCompanyId(companyId);
        for (Employee employee : employees) {
            employee.setActive(false);
            employeeRepository.save(employee);
        }
    }
    
    // Employee üzerinden isActive ve company bilgilerini alma
    public boolean isEmployeeActive(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        return employee.isActive();
    }
    
    public Company getCompanyByEmployeeId(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        Long companyId = employee.getCompanyId();
        return companyService.findById(companyId)
                                .orElseThrow(() -> new RuntimeException("Şirket bulunamadı"));
    }
    
    public Optional<Employee> findByUserId(Long userId) {
        return companyService.findByUserId(userId);
    }
}