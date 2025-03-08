package org.hrmplatform.hrmplatform.dto.response;

import org.hrmplatform.hrmplatform.entity.UserRole;
import org.hrmplatform.hrmplatform.enums.Role;

public record DoLoginResponseDto(
		Role role,
		Long userId,
		String token
		
) {
}