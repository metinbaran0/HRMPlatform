package org.hrmplatform.hrmplatform.service;

import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateEmployeeShiftRequest;
import org.hrmplatform.hrmplatform.entity.EmployeeShift;
import org.hrmplatform.hrmplatform.mapper.EmployeeShiftMapper;
import org.hrmplatform.hrmplatform.repository.EmployeeShiftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeShiftService {
    public final EmployeeShiftRepository employeeShiftRepository;
    public final EmployeeShiftMapper employeeShiftMapper;

    public EmployeeShift createEmployeeShift(CreateEmployeeShiftRequest request, Long employeeId, Long shiftId) {
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

    public boolean deleteEmployeeShift(Long id) {
        Optional<EmployeeShift> employeeShift = employeeShiftRepository.findById(id);
        if (employeeShift.isPresent()) {
            EmployeeShift existingEmployeeShift = employeeShift.get();
            existingEmployeeShift.setIsDeleted(true);
            employeeShiftRepository.save(existingEmployeeShift);
            return true;
        }
        return false;
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

    public void softDeleteEmployeeShift(Long employeeShiftId) {

    }
}
