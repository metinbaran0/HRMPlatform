package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record CreateShiftRequest(
        String name,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
