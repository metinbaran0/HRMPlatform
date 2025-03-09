package org.hrmplatform.hrmplatform.dto.response;

import java.time.LocalDateTime;

public record EmployeeResponseDto(
		Long id,
		String companyName,  // Şirket adı eklendi
		String fullName,// name + surname birleştirildi
		String email,
		String phone,
		String position,
		boolean isActive

) {}