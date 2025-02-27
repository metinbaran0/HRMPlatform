package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
