package org.hrmplatform.hrmplatform.dto.request;

public record UpdateUserRequestDto(
		Long userId,
		String name,
		String email,
		String password

) {

}