package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Break;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BreakRepository extends JpaRepository<Break, Long> {
    List<Break> findByShiftId(Long shiftId);

    List<Break> findByCompanyId(Long companyId);


}
