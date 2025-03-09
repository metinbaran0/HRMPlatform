package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Page<Employee> findByCompanyId(Long companyId, Pageable pageable);

	List<Employee> findByCompanyId(Long companyId);
//	Optional<Employee> findByUserId(Long userId);


	
}