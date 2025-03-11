package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Break;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BreakRepository extends JpaRepository<Break, Long> {
    List<Break> findByShiftId(Long shiftId);

    List<Break> findByCompanyId(Long companyId);


    Optional<Break> findByIdAndCompanyId(Long breakId, Long companyId);

    List<Break> findByShiftIdAndCompanyId(Long shiftId, Long companyId);

}
