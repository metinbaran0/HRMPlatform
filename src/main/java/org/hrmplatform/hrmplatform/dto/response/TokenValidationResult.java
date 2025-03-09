package org.hrmplatform.hrmplatform.dto.response;

public record TokenValidationResult(
         Long authId,
         Long companyId// companyId null olabilir
) {
}