package org.hrmplatform.hrmplatform.mapper;

import org.hrmplatform.hrmplatform.dto.request.CreateEmployeeShiftRequest;
import org.hrmplatform.hrmplatform.entity.EmployeeShift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeShiftMapper {

    @Mapping(target = "employeeId", source = "employeeId")
    @Mapping(target = "shiftId", source = "shiftId")
    @Mapping(target = "shiftDate", source = "request.shiftDate") // assignedDate yerine shiftDate
    @Mapping(target = "isActive", constant = "true") // Yeni vardiya eklenirken aktif olsun
    public EmployeeShift toEmployeeShift(CreateEmployeeShiftRequest request, Long employeeId, Long shiftId, Long companyId);

    @Mapping(target = "employeeId", source = "request.employeeId")
    @Mapping(target = "shiftId", source = "request.shiftId")
    @Mapping(target = "shiftDate", source = "request.shiftDate") // assignedDate yerine shiftDate
    void updateEmployeeShiftFromRequest(CreateEmployeeShiftRequest request, @MappingTarget EmployeeShift employeeShift);
}
