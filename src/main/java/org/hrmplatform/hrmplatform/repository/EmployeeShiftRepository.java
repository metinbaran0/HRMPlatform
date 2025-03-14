package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.EmployeeShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeShiftRepository extends JpaRepository<EmployeeShift, Long> {
    List<EmployeeShift> findByEmployeeId(Long employeeId);




    boolean existsByEmployeeIdAndShiftId(Long employeeId, Long shiftId);

    Optional<EmployeeShift> findByIdAndDeletedFalse(Long employeeShiftId);


    // Tarih aralığına göre vardiya sorgulama
    List<EmployeeShift> findByShiftDateBetween(LocalDate startDate, LocalDate endDate);

    List<EmployeeShift> findByCompanyId(Long companyId);

    List<EmployeeShift> findByCompanyIdAndEmployeeId(Long companyId, Long employeeId);

}
