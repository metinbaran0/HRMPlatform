package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Shift;
import org.hrmplatform.hrmplatform.enums.ShiftType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByCompanyId(Long companyId);

    List<Shift> findByDeletedFalse();

    List<Shift> findByShiftType(ShiftType shiftType);

    List<Shift> findByStartTimeBetween(LocalDate startDate, LocalDate endDate);

    Optional<Shift> findByIdAndCompanyId(Long id, Long companyId);


    List<Shift> findByCompanyIdAndDeletedFalse(Long companyId);



}