package org.hrmplatform.hrmplatform.service;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CreateShiftRequest;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.hrmplatform.hrmplatform.enums.ShiftType;
import org.hrmplatform.hrmplatform.mapper.ShiftMapper;
import org.hrmplatform.hrmplatform.repository.ShiftRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository shiftRepository;
    private final ShiftMapper shiftMapper;


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
}
