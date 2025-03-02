package org.hrmplatform.hrmplatform.mapper;


import org.hrmplatform.hrmplatform.dto.request.CreateShiftRequest;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShiftMapper {

    @Mapping(target = "id", ignore = true)  // ID veritabanı tarafından atanacak
    @Mapping(target = "companyId", expression = "java(companyId)")  // Direkt Long olarak eşleme
    Shift toShift(CreateShiftRequest request, Long companyId);


}
