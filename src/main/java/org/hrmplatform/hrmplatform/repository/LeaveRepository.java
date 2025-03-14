package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
	// Çalışanın ID'si ve izin talebinin durumu (Status) ile sorgulama
	List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, Status status);
	
	// İzin talebinin durumu ile sorgulama
	List<LeaveRequest> findByStatus(Status status);
	
	// Çalışan ve durum ile tek bir izin talebini sorgulama
	LeaveRequest findByEmployeeIdAndStatusAndId(Long employeeId, Status status, Long leaveRequestId);

    boolean existsByEmployeeIdAndStatusAndStartDateBeforeAndEndDateAfter(Long employeeId, Status status, LocalDate shiftStart, LocalDate shiftEnd);
	
	// Şirket ID'si ve izin durumu ile sorgulama yap
	List<LeaveRequest> findByCompanyIdAndStatus(Long companyId, Status status);
}