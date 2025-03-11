package org.hrmplatform.hrmplatform.service;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.response.ExpenseResponseDto;
import org.hrmplatform.hrmplatform.entity.Expense;
import org.hrmplatform.hrmplatform.enums.Status;
import org.hrmplatform.hrmplatform.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
	private final ExpenseRepository expenseRepository;
	
	/**
	 * Çalışan için yeni bir harcama kaydı oluşturur.
	 */
	public ExpenseResponseDto createExpense(Long employeeId, ExpenseResponseDto expenseRequestDTO) {
		LocalDateTime expenseDateTime = expenseRequestDTO.expenseDate();
		
		// Aynı türde onaylanmış harcama kontrolü
		boolean hasApprovedExpense = expenseRepository.findByEmployeeId(employeeId).stream()
		                                              .anyMatch(expense -> expense.getStatus() == Status.APPROVED &&
				                                              expense.getExpenseType().equals(expenseRequestDTO.expenseType()));
		
		if (hasApprovedExpense) {
			throw new IllegalArgumentException("Hata: Aynı türde onaylanmış bir harcamanız zaten var.");
		}
		
		// Harcama objesini oluşturuyoruz
		Expense expense = Expense.builder()
		                         .employeeId(employeeId)
		                         .expenseType(expenseRequestDTO.expenseType())
		                         .amount(expenseRequestDTO.amount())
		                         .expenseDate(expenseDateTime)
		                         .description(expenseRequestDTO.description())
		                         .status(Status.PENDING)
		                         .createdAt(LocalDateTime.now())
		                         .updatedAt(LocalDateTime.now())
		                         .build();
		
		// Harcamayı kaydediyoruz ve DTO'ya çeviriyoruz
		return mapToResponseDTO(expenseRepository.save(expense));
	}
	
	/**
	 * Çalışanın kendi harcamalarını getirir.
	 */
	public List<ExpenseResponseDto> getEmployeeExpenses(Long employeeId) {
		return expenseRepository.findByEmployeeId(employeeId)
		                        .stream()
		                        .map(this::mapToResponseDTO)
		                        .toList();
	}
	
	/**
	 * Şirkete ait tüm harcamaları getirir.
	 */
	public List<ExpenseResponseDto> getAllExpensesByCompany(Long companyId) {
		return expenseRepository.findByCompanyId(companyId)
		                        .stream()
		                        .map(this::mapToResponseDTO)
		                        .toList();
	}
	
	/**
	 * Harcamayı onaylar.
	 */
	public void approveExpense(Long companyId, Long expenseId) {
		Expense expense = expenseRepository.findByIdAndCompanyId(expenseId, companyId)
		                                   .orElseThrow(() -> new IllegalArgumentException("Hata: Bu harcama sizin şirketinize ait değil veya bulunamadı."));
		
		expense.setStatus(Status.APPROVED);
		expense.setUpdatedAt(LocalDateTime.now());
		expenseRepository.save(expense);
	}
	
	/**
	 * Harcamayı reddeder.
	 */
	public void rejectExpense(Long companyId, Long expenseId) {
		Expense expense = expenseRepository.findByIdAndCompanyId(expenseId, companyId)
		                                   .orElseThrow(() -> new IllegalArgumentException("Hata: Bu harcama sizin şirketinize ait değil veya bulunamadı."));
		
		expense.setStatus(Status.REJECTED);
		expense.setUpdatedAt(LocalDateTime.now());
		expenseRepository.save(expense);
	}
	
	
	/**
	 * Expense nesnesini ExpenseResponseDto'ya dönüştürür.
	 */
	private ExpenseResponseDto mapToResponseDTO(Expense expense) {
		return new ExpenseResponseDto(
				expense.getEmployeeId(),
				expense.getExpenseType(),
				expense.getAmount(),
				expense.getExpenseDate(),
				expense.getDescription(),
				expense.getStatus() == Status.APPROVED
		);
	}
}