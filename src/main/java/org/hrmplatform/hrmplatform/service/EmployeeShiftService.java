package org.hrmplatform.hrmplatform.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateEmployeeShiftRequest;
import org.hrmplatform.hrmplatform.entity.*;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.mapper.EmployeeShiftMapper;
import org.hrmplatform.hrmplatform.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeShiftService {
    public final EmployeeShiftRepository employeeShiftRepository;
    public final EmployeeShiftMapper employeeShiftMapper;
    public final EmployeeRepository employeeRepository;
    public final ShiftRepository shiftRepository;
    public final LeaveRepository leaveRepository;
    public final AuthService authService;

    public EmployeeShift createEmployeeShift(String token, CreateEmployeeShiftRequest request, Long employeeId, Long shiftId) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'den şirket ID'sini al

        // Çalışanın şirkete ait olup olmadığını doğrula
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new HRMPlatformException(ErrorType.EMPLOYEE_NOT_FOUND));

        if (!employee.getCompanyId().equals(companyId)) {
            throw new HRMPlatformException(ErrorType.EMPLOYEE_NOT_FOUND_OR_NOT_IN_COMPANY);
        }

        // Geçerli bir vardiya olup olmadığını ve şirketin bu vardiyaya sahip olup olmadığını kontrol et
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new HRMPlatformException(ErrorType.SHIFT_NOT_FOUND));

        if (!shift.getCompanyId().equals(companyId)) {
            throw new HRMPlatformException(ErrorType.SHIFT_NOT_FOUND_IN_COMPANY);
        }

        // Aynı çalışan için aynı vardiyanın atanıp atanmadığını kontrol et
        boolean alreadyAssigned = employeeShiftRepository.existsByEmployeeIdAndShiftId(employeeId, shiftId);
        if (alreadyAssigned) {
            throw new HRMPlatformException(ErrorType.SHIFT_ALREADY_ASSIGNED);
        }

        // Çalışanın izinli olup olmadığını kontrol et
        LocalDate shiftStart = shift.getStartTime(); // Vardiya başlangıç tarihi
        LocalDate shiftEnd = shift.getEndTime(); // Vardiya bitiş tarihi

        // Çalışanın izinli olup olmadığına dair sorgu yapıyoruz
        boolean isOnLeave = leaveRepository.existsByEmployeeIdAndStatusAndStartDateBeforeAndEndDateAfter(
                employeeId, Status.APPROVED, shiftStart, shiftEnd);

        if (isOnLeave) {
            throw new HRMPlatformException(ErrorType.EMPLOYEE_ON_LEAVE);
        }

        // EmployeeShift entity'si oluşturuluyor
        EmployeeShift employeeShift = employeeShiftMapper.toEmployeeShift(request, employeeId, shiftId, companyId);

        // Çalışan vardiyasını veritabanına kaydediyoruz
        return employeeShiftRepository.save(employeeShift);
    }




    // Bütün çalışan vardiyalarını getirme
    public List<EmployeeShift> getAllEmployeeShifts(String token) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'den şirket ID'sini alıyoruz
        return employeeShiftRepository.findByCompanyId(companyId); // Şirkete ait çalışan vardiyalarını getiriyoruz
    }

    // Çalışan ID'sine göre vardiyalar
    public List<EmployeeShift> getEmployeeShiftsByEmployeeId(String token, Long employeeId) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'den şirket ID'sini al

        // Çalışanı veritabanında bul ve şirket ID'sini doğrula
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new HRMPlatformException(ErrorType.EMPLOYEE_NOT_FOUND));

        if (!employee.getCompanyId().equals(companyId)) {
            throw new HRMPlatformException(ErrorType.EMPLOYEE_NOT_FOUND_OR_NOT_IN_COMPANY);
        }

        // Çalışanın vardiyalarını getir
        return employeeShiftRepository.findByEmployeeId(employeeId);
    }

    // Çalışan vardiyasını ID'ye göre getirme
    public EmployeeShift getEmployeeShiftById(Long id) {
        Optional<EmployeeShift> employeeShift = employeeShiftRepository.findById(id);
        return employeeShift.orElseThrow(() -> new HRMPlatformException(ErrorType.DATA_NOT_FOUND));
    }

    // Çalışan vardiyasını soft-delete
    @Transactional
    public void softDeleteEmployeeShift(Long employeeShiftId) {
        // Veritabanından soft delete edilmemiş kaydı buluyoruz
        EmployeeShift employeeShift = employeeShiftRepository.findByIdAndDeletedFalse(employeeShiftId)
                .orElseThrow(() -> new HRMPlatformException(ErrorType.DATA_NOT_FOUND));

        employeeShift.setDeleted(true); // Soft delete işlemi
        employeeShiftRepository.save(employeeShift); // Kaydediyoruz
    }


    // Vardiya tarih aralığına göre filtreleme
    public List<EmployeeShift> getEmployeeShiftsByDateRange(LocalDate startDate, LocalDate endDate) {
        // Veritabanından tarih aralığına göre vardiyaları alıyoruz
        List<EmployeeShift> employeeShifts = employeeShiftRepository.findByShiftDateBetween(startDate, endDate);

        if (employeeShifts.isEmpty()) {
            throw new HRMPlatformException(ErrorType.DATA_NOT_FOUND);
        }

        return employeeShifts;
    }


    public EmployeeShift updateEmployeeShift(String token, Long employeeShiftId, CreateEmployeeShiftRequest request) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'den şirket ID'sini al

        // Güncellenecek vardiyanın var olup olmadığını kontrol et
        EmployeeShift existingShift = employeeShiftRepository.findById(employeeShiftId)
                .orElseThrow(() -> new HRMPlatformException(ErrorType.DATA_NOT_FOUND));

        // Çalışanın şirkete ait olup olmadığını doğrula
        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new HRMPlatformException(ErrorType.EMPLOYEE_NOT_FOUND));

        if (!employee.getCompanyId().equals(companyId)) {
            throw new HRMPlatformException(ErrorType.EMPLOYEE_NOT_FOUND_OR_NOT_IN_COMPANY);
        }

        // Güncellenmek istenen vardiyanın şirkete ait olup olmadığını kontrol et
        Shift shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new HRMPlatformException(ErrorType.SHIFT_NOT_FOUND));

        if (!shift.getCompanyId().equals(companyId)) {
            throw new HRMPlatformException(ErrorType.SHIFT_NOT_FOUND_IN_COMPANY);
        }

        // Vardiya değişmişse, aynı çalışan için aynı vardiyaya atanıp atanmadığını kontrol et
        if (!existingShift.getShiftId().equals(request.shiftId())) {
            boolean alreadyAssigned = employeeShiftRepository.existsByEmployeeIdAndShiftId(request.employeeId(), request.shiftId());
            if (alreadyAssigned) {
                throw new HRMPlatformException(ErrorType.SHIFT_ALREADY_ASSIGNED);
            }
        }

        // Güncellenmek istenen vardiyanın giriş yapan şirketin vardiyası olup olmadığını kontrol et
        if (!existingShift.getCompanyId().equals(companyId)) {
            throw new HRMPlatformException(ErrorType.UNAUTHORIZED_OPERATION);
        }

        // Mapper ile güncelleme işlemi
        employeeShiftMapper.updateEmployeeShiftFromRequest(request, existingShift);

        return employeeShiftRepository.save(existingShift);
    }



    public void validateEmployeeAndShift(Long employeeId, Long shiftId) {
        // Logic to validate if the employee and shift are valid
        if (employeeRepository.findById(employeeId).isEmpty()) {
            throw new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND);
        }
        if (shiftRepository.findById(shiftId).isEmpty()) {
            throw new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND);
        }
    }
}
