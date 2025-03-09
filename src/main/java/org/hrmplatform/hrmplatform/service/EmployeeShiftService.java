package org.hrmplatform.hrmplatform.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateEmployeeShiftRequest;
import org.hrmplatform.hrmplatform.entity.EmployeeShift;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.GlobalExceptionHandler;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.mapper.EmployeeShiftMapper;
import org.hrmplatform.hrmplatform.repository.EmployeeRepository;
import org.hrmplatform.hrmplatform.repository.EmployeeShiftRepository;
import org.hrmplatform.hrmplatform.repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
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

    public EmployeeShift createEmployeeShift(CreateEmployeeShiftRequest request, Long employeeId, Long shiftId) {
        // Aynı çalışan için aynı vardiyanın daha önce atanıp atanmadığını kontrol et
        boolean alreadyAssigned = employeeShiftRepository.existsByEmployeeIdAndShiftId(employeeId, shiftId);

        if (alreadyAssigned) {
            throw new HRMPlatformException(ErrorType.SHIFT_ALREADY_ASSIGNED);
        }

        // Request'ten gelen verilerle EmployeeShift entity'si oluşturuluyor
        EmployeeShift employeeShift = employeeShiftMapper.toEmployeeShift(request, employeeId, shiftId);

        // Shift nesnesini veritabanına kaydediyoruz
        return employeeShiftRepository.save(employeeShift);
    }

    public List<EmployeeShift> getAllEmployeeShifts() {
        return employeeShiftRepository.findAll();
    }

    public List<EmployeeShift> getEmployeeShiftsByEmployeeId(Long employeeId) {
        return employeeShiftRepository.findByEmployeeId(employeeId);
    }

    public EmployeeShift getEmployeeShiftById(Long id) {
        Optional<EmployeeShift> employeeShift = employeeShiftRepository.findById(id);
        return employeeShift.orElse(null);
    }

    @Transactional
    public void softDeleteEmployeeShift(Long employeeShiftId) {
        // Veritabanından ID ve deleted false olan kaydı buluyoruz
        EmployeeShift employeeShift = employeeShiftRepository.findByIdAndDeletedFalse(employeeShiftId)
                .orElseThrow(() -> new HRMPlatformException(ErrorType.DATA_NOT_FOUND));

        employeeShift.setDeleted(true);

        employeeShiftRepository.save(employeeShift);
    }


    public EmployeeShift updateEmployeeShift(Long id, CreateEmployeeShiftRequest request) {
        Optional<EmployeeShift> optionalShift = employeeShiftRepository.findById(id);
        if (optionalShift.isPresent()) {
            EmployeeShift existingShift = optionalShift.get();
            employeeShiftMapper.updateEmployeeShiftFromRequest(request, existingShift);
            return employeeShiftRepository.save(existingShift);
        }
        return null;
    }




    public void validateEmployeeAndShift(Long employeeId, Long shiftId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EntityNotFoundException("Geçersiz çalışan ID.");
        }

        if (!shiftRepository.existsById(shiftId)) {
            throw new EntityNotFoundException("Geçersiz vardiya ID.");
        }
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
}
