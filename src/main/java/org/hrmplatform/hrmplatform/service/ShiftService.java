package org.hrmplatform.hrmplatform.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hrmplatform.hrmplatform.dto.request.CreateShiftRequest;
import org.hrmplatform.hrmplatform.dto.request.ShiftDto;
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
@Slf4j
public class ShiftService {
    private final ShiftRepository shiftRepository;
    private final ShiftMapper shiftMapper;
    private final EmployeeShiftRepository employeeShiftRepository;
    private final AuthService authService;


    public List<Shift> getAllActiveShifts(String token) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'dan şirket ID al

        return shiftRepository.findByCompanyIdAndDeletedFalse(companyId); // Silinmemişleri getir
    }


    // Tüm vardiyaları getirme (giriş yapan kullanıcının companyId'sine göre)
    public List<Shift> getAllShifts(String token) {
        // AuthService üzerinden companyId alıyoruz
        Long companyId = authService.getCompanyIdFromToken(token);

        // Şirkete ait vardiyaları getiriyoruz
        return shiftRepository.findByCompanyId(companyId);
    }

    public Shift getShiftById(Long id) {
        Optional<Shift> shift = shiftRepository.findById(id);
        return shift.orElse(null);
    }

    public boolean deleteShift(Long id, Long companyId) {
        Optional<Shift> shiftOptional = shiftRepository.findByIdAndCompanyId(id, companyId); // Yetkili şirket mi?

        if (!shiftOptional.isPresent()) {
            log.warn("Silme başarısız: Shift ID={} Company ID={} - Vardiya bulunamadı veya Yetkisiz Erişim!", id, companyId);
            return false; // Vardiya yoksa veya companyId eşleşmiyorsa silme başarısız olur.
        }

        Shift shift = shiftOptional.get();
        shift.setDeleted(true); // Soft delete
        shiftRepository.save(shift);

        log.info("Vardiya başarıyla silindi: Shift ID={} Company ID={}", id, companyId);
        return true;
    }

    // Vardiya güncelleme (giriş yapan kullanıcının companyId'sine göre)
    public Shift updateShift(String token, Long id, ShiftDto request) {
        // Token'dan companyId al
        Long companyId = authService.getCompanyIdFromToken(token);

        // Şirkete ait olup olmadığını kontrol et
        Shift existingShift = shiftRepository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new IllegalArgumentException("Vardiya bulunamadı veya bu şirkete ait değil."));

        // Mapper kullanarak mevcut Shift nesnesini güncelle
        shiftMapper.updateFromDto(request, existingShift);

        // Güncellenmiş vardiyayı kaydet ve döndür
        return shiftRepository.save(existingShift);
    }


    //vardiya türüne göre vardiya listeleme
    public List<Shift> getShiftsByShiftType(ShiftType shiftType) {
        return shiftRepository.findByShiftType(shiftType);

    }

    public List<Shift> getShiftsByDateRange(LocalDate startDate, LocalDate endDate) {
        return shiftRepository.findByStartTimeBetween(startDate, endDate);
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

    @Transactional
    public ShiftDto createShift(String token, ShiftDto shiftDto) {
        Long companyId = authService.getCompanyIdFromToken(token); // AuthService üzerinden companyId alındı

        Shift shift = Shift.builder()
                .companyId(companyId)
                .shiftName(shiftDto.shiftName())
                .startTime(shiftDto.startTime())
                .endTime(shiftDto.endTime())
                .shiftType(shiftDto.shiftType())
                .build();

        Shift savedShift = shiftRepository.save(shift);
        return shiftMapper.toShiftDTO(savedShift);
    }


}
