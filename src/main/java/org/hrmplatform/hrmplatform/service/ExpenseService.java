package org.hrmplatform.hrmplatform.service;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.response.ExpenseResponseDto;
import org.hrmplatform.hrmplatform.entity.Expense;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
	private final ExpenseRepository expenseRepository;
	
	public ExpenseResponseDto createExpense(ExpenseResponseDto expenseRequestDTO) {
		// expenseDate bir LocalDate olduğu varsayılmıştır
		LocalDateTime expenseDateTime = expenseRequestDTO.expenseDate();
		
		// Aynı türde ve onaylanmış harcama olup olmadığını kontrol et
		List<Expense> existingApprovedExpenses = expenseRepository.findByEmployeeId(expenseRequestDTO.employeeId())
		                                                          .stream()
		                                                          .filter(expense -> expense.getStatus() == Status.APPROVED &&
				                                                          expense.getExpenseType().equals(expenseRequestDTO.expenseType()))
		                                                          .toList();
		
		if (!existingApprovedExpenses.isEmpty()) {
			throw new IllegalArgumentException("Hata: Aynı türde onaylanmış bir harcamanız zaten var.");
		}
		
		Expense expense = Expense.builder()
		                         .employeeId(expenseRequestDTO.employeeId())
		                         .expenseType(expenseRequestDTO.expenseType())
		                         .amount(expenseRequestDTO.amount())
		                         .expenseDate(expenseDateTime)
		                         .description(expenseRequestDTO.description())
		                         .status(Status.PENDING) // Varsayılan olarak beklemede
		                         .createdAt(LocalDateTime.now())
		                         .updatedAt(LocalDateTime.now())
		                         .build();
		
		return mapToResponseDTO(expenseRepository.save(expense));
	}
	
	// Expense nesnesini ExpenseResponseDto'ya dönüştüren metot
	private ExpenseResponseDto mapToResponseDTO(Expense expense) {
		return new ExpenseResponseDto(
				expense.getEmployeeId(),
				expense.getExpenseType(),
				expense.getAmount(),
				expense.getExpenseDate(),
				expense.getDescription(),
				expense.getStatus() == Status.APPROVED // Yeni eklediğimiz harcama status.pending ise false döner.
				// (dto içinde boolean olarak tanımladığımız için.
		);
	}
	
	public List<ExpenseResponseDto> getAllExpenses() {
		return expenseRepository.findAll()
		                        .stream()
		                        .map(this::mapToResponseDTO) // Expense → ExpenseResponseDto dönüşümü
		                        .toList();
	}
	
}