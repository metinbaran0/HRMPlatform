package org.hrmplatform.hrmplatform.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.BreakRequestDto;
import org.hrmplatform.hrmplatform.entity.Break;
import org.hrmplatform.hrmplatform.exception.ErrorType;
import org.hrmplatform.hrmplatform.exception.HRMPlatformException;
import org.hrmplatform.hrmplatform.mapper.BreakMapper;
import org.hrmplatform.hrmplatform.repository.BreakRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BreakService {
    private final BreakRepository breakRepository;
    private final BreakMapper breakMapper;
    private final AuthService authService;


    @Transactional
    public Break createBreak(String token, BreakRequestDto breakDTO) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'dan şirket kimliği alınıyor

        Break breakEntity = breakMapper.fromBreakDto(breakDTO);
        breakEntity.setCompanyId(companyId); // DTO içinde companyId yoksa burada set ediliyor

        return breakRepository.save(breakEntity);
    }

    @Transactional
    public List<Break> getAllBreaks(String token) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'dan şirket kimliği alınıyor

        return breakRepository.findByCompanyId(companyId); // Şirket kimliğiyle eşleşen tüm molaları getiriyoruz
    }
    @Transactional
    public Optional<Break> getBreakById(String token, Long breakId) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token'dan şirket kimliği alınıyor

        return breakRepository.findByIdAndCompanyId(breakId, companyId); // Hem ID'ye hem de şirket kimliğine göre sorgulama yapılıyor
    }


    public List<Break> getBreaksByShiftId(String token, Long shiftId) {
        Long companyId = authService.getCompanyIdFromToken(token); // Token üzerinden companyId alınıyor
        return breakRepository.findByShiftIdAndCompanyId(shiftId, companyId); // Vardiya ve şirket ID'ye göre sorgulama
    }
    public List<Break> getBreaksByCompany(String token, Long companyId) {
        Long currentCompanyId = authService.getCompanyIdFromToken(token); // Token üzerinden companyId alınıyor
        if (!companyId.equals(currentCompanyId)) {
            throw new HRMPlatformException(ErrorType.COMPANY_NOT_FOUND);
        }
        return breakRepository.findByCompanyId(companyId); // Şirket ID'ye göre molalar getiriliyor
    }


    public Break updateBreak(Long breakId, BreakRequestDto breakDTO) {
        Break existingBreak = breakRepository.findById(breakId)
                .orElseThrow(() -> new RuntimeException(ErrorType.DATA_NOT_FOUND.getMessage()));
        breakMapper.updateBreakFromDto(breakDTO, existingBreak);
        return breakRepository.save(existingBreak);
    }

    public void softDeleteBreak(Long breakId) {
        Optional<Break> optionalBreak = breakRepository.findById(breakId);

        if (optionalBreak.isPresent()) {
            Break existingBreak = optionalBreak.get();
            existingBreak.setDeleted(true);
            breakRepository.save(existingBreak);
        } else {
            throw new RuntimeException(ErrorType.DATA_NOT_FOUND.getMessage());
        }

    }

}

