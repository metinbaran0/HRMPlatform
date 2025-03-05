package org.hrmplatform.hrmplatform.dto.request;

import lombok.Data;
import java.time.LocalDateTime;


public record CreateEmployeeShiftRequest(
        Long employeeId,
        Long shiftId,
        LocalDateTime assignedDate) {

}
