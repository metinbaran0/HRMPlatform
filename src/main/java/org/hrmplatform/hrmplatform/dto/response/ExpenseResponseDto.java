package org.hrmplatform.hrmplatform.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseResponseDto(
                                 Long employeeId,
                                 String expenseType,
                                 BigDecimal amount,
                                 LocalDateTime expenseDate,
                                 String description,
                                 Boolean status) {
}