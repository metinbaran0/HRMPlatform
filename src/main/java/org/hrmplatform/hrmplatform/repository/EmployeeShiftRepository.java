package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.EmployeeShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift, Long> {
    List<EmployeeShift> findByEmployeeId(Long employeeId);


    List<EmployeeShift> findByEmployeeIdAndAssignedDate(Long employeeId, LocalDate date);

}
