package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	Page<Employee> findByCompanyId(Long companyId, Pageable pageable);

	List<Employee> findByCompanyId(Long companyId);
//	Optional<Employee> findByUserId(Long userId);
    List<Employee> findByNameContainingIgnoreCase(String name);
	
	@Query("SELECT e.id FROM Employee e WHERE LOWER(e.name) = LOWER(:name)")
	Long findEmployeeIdByName(@Param("name") String name);
	
}