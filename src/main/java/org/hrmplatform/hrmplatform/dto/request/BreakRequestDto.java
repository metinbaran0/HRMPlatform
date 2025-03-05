package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDateTime;

public record BreakRequestDto(
        Long shiftId,
        Long companyId,
        String breakName,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
