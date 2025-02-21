package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
	List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);
	List<LeaveRequest> findByStatus(LeaveStatus status);
}