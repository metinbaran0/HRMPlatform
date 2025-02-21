package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.LeaveRequest;
import org.hrmplatform.hrmplatform.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
	List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, Status status);
	List<LeaveRequest> findByStatus(Status status);
}