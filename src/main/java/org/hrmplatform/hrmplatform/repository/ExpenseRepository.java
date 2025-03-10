package org.hrmplatform.hrmplatform.repository;


import org.hrmplatform.hrmplatform.entity.Expense;
import org.hrmplatform.hrmplatform.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByEmployeeId(Long employeeId);
	List<Expense> findByStatus(Status status);
}