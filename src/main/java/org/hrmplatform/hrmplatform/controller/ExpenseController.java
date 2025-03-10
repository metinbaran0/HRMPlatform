package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.dto.response.ExpenseResponseDto;
import org.hrmplatform.hrmplatform.entity.Expense;
import org.hrmplatform.hrmplatform.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(EXPENSE)
@RequiredArgsConstructor
public class ExpenseController {
	
	private final ExpenseService expenseService;
	
	@PostMapping(CREATE_EXPENSE)
	public ResponseEntity<BaseResponse<ExpenseResponseDto>> createExpense(@RequestBody ExpenseResponseDto expenseRequestDTO) {
		if (expenseRequestDTO == null || expenseRequestDTO.amount() == null || expenseRequestDTO.amount()
		                                                                                        .compareTo(BigDecimal.ZERO) <= 0) {
			return ResponseEntity.badRequest().body(
					BaseResponse.<ExpenseResponseDto>builder()
					            .code(400)
					            .success(false)
					            .message("Harcama miktarı geçersiz veya boş olamaz!")
					            .build()
			);
		}
		
		ExpenseResponseDto createdExpense = expenseService.createExpense(expenseRequestDTO);
		return ResponseEntity.ok(
				BaseResponse.<ExpenseResponseDto>builder()
				            .code(200)
				            .success(true)
				            .message("Harcama başarıyla oluşturuldu.")
				            .data(createdExpense)
				            .build()
		);
	}
	
	@GetMapping(GETALL_EXPENSE)
	public ResponseEntity<BaseResponse<List<ExpenseResponseDto>>> getAllExpense(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		List<ExpenseResponseDto> expenses = expenseService.getAllExpenses();
		
		return ResponseEntity.ok(
				BaseResponse.<List<ExpenseResponseDto>>builder()
				            .code(200)
				            .success(true)
				            .data(expenses)
				            .message("Tüm harcamalar başarıyla getirildi.")
				            .build()
		);
	}
	
}