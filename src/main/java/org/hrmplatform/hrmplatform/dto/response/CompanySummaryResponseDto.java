package org.hrmplatform.hrmplatform.dto.response;

public record CompanySummaryResponseDto(int totalCompanies,
                                        int totalAdmins,
                                       int totalEmployees
                                        ) {
}