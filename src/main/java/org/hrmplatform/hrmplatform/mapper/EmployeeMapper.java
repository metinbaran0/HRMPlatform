package org.hrmplatform.hrmplatform.mapper;

import org.hrmplatform.hrmplatform.dto.request.EmployeeUpdateDto;
import org.hrmplatform.hrmplatform.entity.Employee;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {

    EmployeeMapper Instance = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(target = "id", ignore = true)
    void updateEmployeeFromDto(EmployeeUpdateDto dto, @MappingTarget Employee employee);
}
