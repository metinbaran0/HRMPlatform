package org.hrmplatform.hrmplatform.service;

import lombok.AllArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.BreakRequestDto;
import org.hrmplatform.hrmplatform.entity.Break;
import org.hrmplatform.hrmplatform.exception.ErrorType;
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


    public Break createBreak(BreakRequestDto breakDTO) {
        Break breakEntity = breakMapper.fromBreakDto(breakDTO);
        return breakRepository.save(breakEntity);
    }

    public List<Break> getAllBreaks() {
        return breakRepository.findAll();
    }
    public Optional<Break> getBreakById(Long breakId) {
        return breakRepository.findById(breakId);
    }


    public List<Break> getBreaksByShiftId(Long shiftId) {
        return breakRepository.findByShiftId(shiftId);
    }

    public List<Break> getBreaksByCompany(Long companyId) {
        return breakRepository.findByCompanyId(companyId);
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

