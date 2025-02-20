package org.hrmplatform.hrmplatform.dto.response;

import java.time.LocalDateTime;

public record UserProfileResponseDto(
		String name,
		String email,
		Boolean status,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {

}