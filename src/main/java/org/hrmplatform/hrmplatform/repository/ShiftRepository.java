package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByCompanyId(Long companyId);

    List<Shift> findByDeletedFalse();
}
