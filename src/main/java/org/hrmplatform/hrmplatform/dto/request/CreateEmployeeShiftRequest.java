package org.hrmplatform.hrmplatform.dto.request;

import java.time.LocalDate;


public record CreateEmployeeShiftRequest(
        Long employeeId,
        Long shiftId,
        LocalDate assignedDate) {

}
