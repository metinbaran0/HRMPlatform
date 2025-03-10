package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDateTime;

public record EmployeeShiftDto(
        Long employeeId,
        Long shiftId,
        LocalDateTime assignedDate,
        Boolean isActive
) {
}
