package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	List<Employee> findByCompanyId(Long companyId);
	
}