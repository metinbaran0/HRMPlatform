package org.hrmplatform.hrmplatform.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hrmplatform.hrmplatform.enums.LeaveStatus;
import org.hrmplatform.hrmplatform.enums.LeaveType;

import java.time.LocalDate;

public record LeaveRequestDto(
		@NotNull(message = "Başlangıç tarihi boş geçilemez.")
		LocalDate startDate,
		@NotNull(message = "Bitiş tarihi boş geçilemez.")
		LocalDate endDate,
		LeaveType leaveType,
		@NotNull(message = "Çalışan ID boş olamaz.")
		Long employeeId
) {
}