package org.hrmplatform.hrmplatform.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record BreakRequestDto(
        Long shiftId,
        String breakName,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
