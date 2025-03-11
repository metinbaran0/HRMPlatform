package org.hrmplatform.hrmplatform.mapper;

import org.hrmplatform.hrmplatform.dto.request.BreakRequestDto;
import org.hrmplatform.hrmplatform.entity.Break;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BreakMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "shiftId", source = "dto.shiftId")
    @Mapping(target = "breakName", source = "dto.breakName")
    @Mapping(target = "startTime", source = "dto.startTime")
    @Mapping(target = "endTime", source = "dto.endTime")
    Break fromBreakDto(BreakRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "shiftId", source = "dto.shiftId")
    @Mapping(target = "breakName", source = "dto.breakName")
    @Mapping(target = "startTime", source = "dto.startTime")
    @Mapping(target = "endTime", source = "dto.endTime")
    void updateBreakFromDto(BreakRequestDto dto, @MappingTarget Break breakEntity);
}
