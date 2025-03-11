package org.hrmplatform.hrmplatform.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateEmployeeShiftRequest;
import org.hrmplatform.hrmplatform.entity.EmployeeShift;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.mapper.EmployeeShiftMapper;
import org.hrmplatform.hrmplatform.repository.EmployeeRepository;
import org.hrmplatform.hrmplatform.repository.EmployeeShiftRepository;
import org.hrmplatform.hrmplatform.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeShiftService {
    public final EmployeeShiftRepository employeeShiftRepository;
    public final EmployeeShiftMapper employeeShiftMapper;
    public final EmployeeRepository employeeRepository;
    public final ShiftRepository shiftRepository;
    public final AuthService authService;

    // Çalışan vardiyası oluşturma
    public EmployeeShift createEmployeeShift(String token, CreateEmployeeShiftRequest request, Long employeeId, Long shiftId) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'den şirket ID'sini alıyoruz.

        // Geçerli bir çalışan olup olmadığını ve çalışanı doğru şirkete ait olup olmadığını kontrol et
        if (!employeeRepository.existsByIdAndCompanyId(employeeId, companyId)) {
            throw new HRMPlatformException(ErrorType.EMPLOYEE_NOT_FOUND_OR_NOT_IN_COMPANY);
        }

        // Geçerli bir vardiya olup olmadığını kontrol et
        if (!shiftRepository.existsById(shiftId)) {
            throw new HRMPlatformException(ErrorType.SHIFT_NOT_FOUND);
        }

        // Aynı çalışan için aynı vardiyanın atanıp atanmadığını kontrol et
        boolean alreadyAssigned = employeeShiftRepository.existsByEmployeeIdAndShiftId(employeeId, shiftId);
        if (alreadyAssigned) {
            throw new HRMPlatformException(ErrorType.SHIFT_ALREADY_ASSIGNED);
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
    public List<EmployeeShift> getEmployeeShiftsByEmployeeId(Long employeeId) {
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


    // Çalışan vardiyasını güncelleme
    public EmployeeShift updateEmployeeShift(Long id, CreateEmployeeShiftRequest request) {
        Optional<EmployeeShift> optionalShift = employeeShiftRepository.findById(id);
        if (optionalShift.isPresent()) {
            EmployeeShift existingShift = optionalShift.get();
            employeeShiftMapper.updateEmployeeShiftFromRequest(request, existingShift);
            return employeeShiftRepository.save(existingShift); // Güncellenmiş vardiya kaydedilir
        } else {
            throw new HRMPlatformException(ErrorType.DATA_NOT_FOUND);
        }
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
