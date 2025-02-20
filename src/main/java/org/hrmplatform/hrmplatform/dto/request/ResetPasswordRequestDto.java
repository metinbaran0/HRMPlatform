package org.hrmplatform.hrmplatform.dto.request;

import jakarta.validation.constraints.*;

public record ResetPasswordRequestDto (
		@NotBlank(message = "Email boş geçilemez.")
		String email
) {}