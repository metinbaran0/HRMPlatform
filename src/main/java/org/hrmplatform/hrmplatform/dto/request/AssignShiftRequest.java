package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDateTime;

public record AssignShiftRequest(
        Long shiftId,
        Long employeeId,
        LocalDateTime date
) {
}
