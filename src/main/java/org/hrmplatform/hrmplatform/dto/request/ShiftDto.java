package org.hrmplatform.hrmplatform.dto.request;

import org.hrmplatform.hrmplatform.enums.ShiftType;

import java.time.LocalDate;

public record ShiftDto(
        String shiftName,
        LocalDate startTime,
        LocalDate endTime,
        ShiftType shiftType
) {
}
