package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDate;

public record BreakRequestDto(
        Long shiftId,
        Long companyId,
        String breakName,
        LocalDate startTime,
        LocalDate endTime
) {
}
