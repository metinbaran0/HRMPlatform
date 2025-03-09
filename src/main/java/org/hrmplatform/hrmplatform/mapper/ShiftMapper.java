package org.hrmplatform.hrmplatform.mapper;


import org.hrmplatform.hrmplatform.dto.request.CreateShiftRequest;
import org.hrmplatform.hrmplatform.dto.request.ShiftDto;
import org.hrmplatform.hrmplatform.entity.Company;
import org.hrmplatform.hrmplatform.entity.Shift;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShiftMapper {

    // CreateShiftRequest -> Shift dönüşümü
    @Mapping(target = "id", ignore = true)  // ID veritabanı tarafından atanacak
    @Mapping(target = "companyId", expression = "java(companyId)")  // companyId'yi dışarıdan alıyoruz
    Shift toShift(CreateShiftRequest request, Long companyId);

    // Shift -> ShiftDTO dönüşümü
    @Mapping(target = "shiftName", source = "shiftName")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    @Mapping(target = "shiftType", source = "shiftType")
    ShiftDto toShiftDTO(Shift shift);

}
