package org.hrmplatform.hrmplatform.service;

import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.EmployeeRequestDto;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.hrmplatform.hrmplatform.exception.EmployeeNotFoundException;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.repository.EmployeeRepository;
import org.hrmplatform.hrmplatform.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {
   private final EmployeeRepository employeeRepository;
   private final EmailService emailService;
   private final CompanyService companyService;

   /**
    * Tüm çalışanları getirir (sayfalama eklenmiştir).
    * Veritabanını yormamak için sayfalama kullanıyoruz.
    */
   public List<Employee> getAllEmployees(int page, int size) {
      List<Employee> allEmployees = employeeRepository.findAll(); // Tüm çalışanları çekiyoruz
      return PaginationUtil.paginate(allEmployees, page, size); // Sayfalama işlemini uyguluyoruz
   }

   public Employee createEmployee(EmployeeRequestDto request) {
      // Company var mı kontrol et
      Company company = companyService.findByCompanyId(request.companyId())
              .orElseThrow(() -> new IllegalArgumentException("Company not found with ID: " + request.companyId()));

      Employee employee = Employee.builder()
              .companyId(request.companyId())
              .name(request.name())
              .surname(request.surname())
              .email(request.email())
              .phone(request.phone())
              .position(request.position())
              .isActive(false) // Yeni çalışan otomatik olarak pasif başlasın
              .createdAt(LocalDateTime.now())
              .updatedAt(LocalDateTime.now())
              .build();

      return employeeRepository.save(employee);
   }

   /**
    * Mevcut bir çalışanı günceller.
    * Çalışan bulunamazsa hata döndürülür.
    */
   public Employee updateEmployee(Long id, Employee employeeDetails) {
      Employee employee = employeeRepository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(ErrorType.EMPLOYEE_NOT_FOUND));
      employee.setName(employeeDetails.getName());
      employee.setSurname(employeeDetails.getSurname());
      employee.setEmail(employeeDetails.getEmail());
      employee.setPhone(employeeDetails.getPhone());
      employee.setPosition(employeeDetails.getPosition());

      // isActive değişmemeli, çünkü onun için ayrı bir method var!
      employee.setUpdatedAt(LocalDateTime.now());

      return employeeRepository.save(employee);
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
}
