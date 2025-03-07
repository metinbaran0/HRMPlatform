package org.hrmplatform.hrmplatform.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateShiftRequest;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.EmployeeShift;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.hrmplatform.hrmplatform.enums.ShiftType;
import org.hrmplatform.hrmplatform.mapper.ShiftMapper;
import org.hrmplatform.hrmplatform.repository.EmployeeShiftRepository;
import org.hrmplatform.hrmplatform.repository.ShiftRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class ShiftService {
    private final ShiftRepository shiftRepository;
    private final ShiftMapper shiftMapper;
    private final EmployeeShiftRepository employeeShiftRepository;



  /*  public Shift createShift(CreateShiftRequest request, Long companyId) {
        Shift shift = shiftMapper.toShift(request,companyId);
        return shiftRepository.save(shift);
    }*/

    public Shift createShift(CreateShiftRequest request, Long companyId, ShiftType shiftType) {
        // Request'ten gelen verilerle Shift entity'si oluşturuluyor
        Shift shift = shiftMapper.toShift(request, companyId);

        // Vardiya türünü (ShiftType) atıyoruz
        shift.setShiftType(shiftType);

        // Vardiya süresini hesaplıyoruz (başlangıç ve bitiş saatlerine göre)
        if (shift.getStartTime() != null && shift.getEndTime() != null) {
            shift.setDurationInMinutes((int) java.time.Duration.between(shift.getStartTime(), shift.getEndTime()).toMinutes());
        }

        // Shift nesnesini veritabanına kaydediyoruz
        return shiftRepository.save(shift);
    }


    public List<Shift> getAllShifts() {
        return shiftRepository.findAll();
    }

    public List<Shift> getAllActiveShifts() {
        return shiftRepository.findByDeletedFalse(); // Silinmemiş vardiyaları alıyoruz
    }


    public List<Shift> getShiftsByCompanyId(Long companyId) {
        return shiftRepository.findByCompanyId(companyId);
    }

    public Shift getShiftById(Long id) {
        Optional<Shift> shift = shiftRepository.findById(id);
        return shift.orElse(null);
    }

    public boolean deleteShift(Long id) {
        Optional<Shift> shift = shiftRepository.findById(id);
        if (shift.isPresent()) {
            Shift existingShift = shift.get();
            existingShift.setDeleted(true);
            shiftRepository.save(existingShift);
            return true;
        }
        return false;
    }

    public Shift updateShift(Long id, CreateShiftRequest request) {
        return null;
    }


    //vardiya türüne göre vardiya listeleme
    public List<Shift> getShiftsByShiftType(ShiftType shiftType) {
        return shiftRepository.findByShiftType(shiftType);

    }

    public List<Shift> getShiftsByDateRange(LocalDate startDate, LocalDate endDate) {
        return shiftRepository.findByStartTimeBetween(startDate, endDate);
    }

    /**
     *
     * TODO : Bu metodu gözden geçir
     */
    // Vardiya çakışmasını kontrol etme metodu
    public boolean checkShiftConflict(Long employeeId, LocalDate date) {
        // Çalışanın o gün alacağı vardiya listesini al
        List<EmployeeShift> employeeShifts = employeeShiftRepository.findByEmployeeIdAndAssignedDate(employeeId, date);

        // Eğer listede birden fazla vardiya varsa, çakışma vardır
        return employeeShifts.size() > 1;
    }




    // Vardiya dağılımını sorgulayan metod
    public Map<Long, List<Shift>> getShiftDistribution() {
        // Çalışanların tüm vardiyalarını al
        List<EmployeeShift> employeeShifts = employeeShiftRepository.findAll();

        // Vardiya dağılımı için bir harita oluştur
        Map<Long, List<Shift>> shiftDistribution = new HashMap<>();

        // Her çalışanın vardiyalarını sorgula
        for (EmployeeShift employeeShift : employeeShifts) {
            Long employeeId = employeeShift.getEmployeeId();

            // Vardiya bilgilerini al
            Shift shift = shiftRepository.findById(employeeShift.getShiftId()).orElse(null);

            // Eğer vardiya mevcutsa, çalışanın vardiya listesine ekle
            if (shift != null) {
                shiftDistribution.computeIfAbsent(employeeId, k -> new ArrayList<>()).add(shift);
            }
        }

        return shiftDistribution;
    }
}
