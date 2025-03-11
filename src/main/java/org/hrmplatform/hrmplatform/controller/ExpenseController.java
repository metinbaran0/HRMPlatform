package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.dto.response.ExpenseResponseDto;
import org.hrmplatform.hrmplatform.entity.Expense;
import org.hrmplatform.hrmplatform.service.AuthService;
import org.hrmplatform.hrmplatform.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(EXPENSE)
@RequiredArgsConstructor
@CrossOrigin("*")
public class ExpenseController {
	
	private final ExpenseService expenseService;
	private final AuthService authService;
	
	/**
	 * Çalışan tarafından harcama talebi oluşturulur.
	 */
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@PostMapping(CREATE_EXPENSE)
	public ResponseEntity<BaseResponse<ExpenseResponseDto>> createExpense(
			@RequestHeader("Authorization") String token,
			@RequestBody ExpenseResponseDto expenseRequestDTO
	) {
		if (expenseRequestDTO == null || expenseRequestDTO.amount() == null ||
				expenseRequestDTO.amount().compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.badRequest().body(
					BaseResponse.<ExpenseResponseDto>builder()
					            .code(400)
					            .success(false)
					            .message("Harcama miktarı geçersiz veya boş olamaz!")
					            .build()
			);
		}
		
		// Token'dan employeeId'yi alıyoruz
		Long employeeId = authService.getEmployeeIdFromToken(token);
		
		// Harcama işlemini oluşturuyoruz
		ExpenseResponseDto createdExpense = expenseService.createExpense(employeeId, expenseRequestDTO);
		
		return ResponseEntity.ok(
				BaseResponse.<ExpenseResponseDto>builder()
				            .code(200)
				            .success(true)
				            .message("Harcama başarıyla oluşturuldu.")
				            .data(createdExpense)
				            .build()
		);
	}
	
	
	/**
	 * Çalışan sadece kendi harcamalarını görebilir.
	 */
	@PreAuthorize("hasAuthority('EMPLOYEE')")
	@GetMapping(GET_MY_EXPENSES)
	public ResponseEntity<BaseResponse<List<ExpenseResponseDto>>> getMyExpenses(
			@RequestHeader("Authorization") String token
	) {
		Long employeeId = authService.getEmployeeIdFromToken(token);
		List<ExpenseResponseDto> expenses = expenseService.getEmployeeExpenses(employeeId);
		
		return ResponseEntity.ok(
				BaseResponse.<List<ExpenseResponseDto>>builder()
				            .code(200)
				            .success(true)
				            .data(expenses)
				            .message("Kendi harcamalarınız başarıyla getirildi.")
				            .build()
		);
	}
	
	/**
	 * Şirket yöneticisi tüm harcamaları görebilir.
	 */
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@GetMapping(GETALL_EXPENSE)
	public ResponseEntity<BaseResponse<List<ExpenseResponseDto>>> getAllExpenses(
			@RequestHeader("Authorization") String token
	) {
		Long companyId = authService.getCompanyIdFromToken(token);
		List<ExpenseResponseDto> expenses = expenseService.getAllExpensesByCompany(companyId);
		
		return ResponseEntity.ok(
				BaseResponse.<List<ExpenseResponseDto>>builder()
				            .code(200)
				            .success(true)
				            .data(expenses)
				            .message("Şirkete ait tüm harcamalar başarıyla getirildi.")
				            .build()
		);
	}
	
	/**
	 * Şirket yöneticisi bir harcamayı onaylayabilir.
	 */
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@PutMapping(APPROVE_EXPENSE)
	public ResponseEntity<BaseResponse<Void>> approveExpense(
			@RequestHeader("Authorization") String token,
			@PathVariable Long expenseId
	) {
		Long companyId = authService.getCompanyIdFromToken(token);
		expenseService.approveExpense(companyId, expenseId);
		
		return ResponseEntity.ok(
				BaseResponse.<Void>builder()
				            .code(200)
				            .success(true)
				            .message("Harcama başarıyla onaylandı.")
				            .build()
		);
	}
	
	/**
	 * Şirket yöneticisi bir harcamayı reddedebilir.
	 */
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')")
	@PutMapping(REJECT_EXPENSE)
	public ResponseEntity<BaseResponse<Void>> rejectExpense(
			@RequestHeader("Authorization") String token,
			@PathVariable Long expenseId
	) {
		Long companyId = authService.getCompanyIdFromToken(token);
		expenseService.rejectExpense(companyId, expenseId);
		
		return ResponseEntity.ok(
				BaseResponse.<Void>builder()
				            .code(200)
				            .success(true)
				            .message("Harcama başarıyla reddedildi.")
				            .build()
		);
	}
}