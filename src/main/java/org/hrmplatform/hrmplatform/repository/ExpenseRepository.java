package org.hrmplatform.hrmplatform.repository;


import org.hrmplatform.hrmplatform.entity.Expense;
import org.hrmplatform.hrmplatform.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByEmployeeId(Long employeeId);
	List<Expense> findByStatus(Status status);
	// Belirli bir şirkete ait harcamaları getir
	List<Expense> findByCompanyId(Long companyId);
	Optional<Expense> findByIdAndCompanyId(Long expenseId, Long companyId);
}