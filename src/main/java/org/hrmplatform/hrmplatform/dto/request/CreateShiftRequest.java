package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record CreateShiftRequest(
        String name,
        LocalDate startTime,
        LocalDate endTime
) {
}
