//package org.hrmplatform.hrmplatform.mapper;
//
//import org.hrmplatform.hrmplatform.dto.request.CompanyDto;
//import org.hrmplatform.hrmplatform.entity.Company;
//import org.mapstruct.*;
//import org.mapstruct.factory.Mappers;
//
//@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
//        unmappedTargetPolicy = ReportingPolicy.IGNORE)
//public interface CompanyMapper {
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "status", expression = "java(org.hrmplatform.hrmplatform.enums.Status.PENDING)")
//    @Mapping(target = "subscriptionEndDate", ignore = true)
//    Company fromCompanyDto(CompanyDto dto);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
//    @Mapping(target = "status", ignore = true)
//    @Mapping(target = "subscriptionEndDate", ignore = true)
//    @Mapping(target = "id", ignore = true) // ID değişmeyeceği için güncellemeye dahil etme
//    void updateCompanyFromDto(CompanyDto dto, @MappingTarget Company company);
//
//}
//
//


package org.hrmplatform.hrmplatform.mapper;

import org.hrmplatform.hrmplatform.dto.request.CompanyDto;
import org.hrmplatform.hrmplatform.entity.Company;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompanyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(org.hrmplatform.hrmplatform.enums.Status.PENDING)")
    @Mapping(target = "subscriptionEndDate", ignore = true)
    @Mapping(target = "contactPerson", source = "dto.contactPerson")
    @Mapping(target = "sector", source = "dto.sector")
    @Mapping(target = "employeeCount", source = "dto.employeeCount")
    Company fromCompanyDto(CompanyDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "subscriptionEndDate", ignore = true)
    @Mapping(target = "contactPerson", source = "dto.contactPerson")
    @Mapping(target = "sector", source = "dto.sector")
    @Mapping(target = "employeeCount", source = "dto.employeeCount")
    void updateCompanyFromDto(CompanyDto dto, @MappingTarget Company company);
}

