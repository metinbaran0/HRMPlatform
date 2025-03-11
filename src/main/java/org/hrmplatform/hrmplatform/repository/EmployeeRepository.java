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
	
	// E-posta ile çalışanı bulma
	@Query("SELECT e.id FROM Employee e WHERE LOWER(e.email) = LOWER(:email)")
	Long findEmployeeIdByEmail(@Param("email") String email);
	
	
	// Email'e göre çalışan ID'sini döndüren metot
	Optional<Employee> findByEmail(String email);


	boolean existsByIdAndCompanyId(Long employeeId, Long companyId);
}