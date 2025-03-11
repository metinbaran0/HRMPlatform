package org.hrmplatform.hrmplatform.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseRequestDto(
		String expenseType,
		BigDecimal amount,
		LocalDateTime expenseDate,
		String description
) {
}