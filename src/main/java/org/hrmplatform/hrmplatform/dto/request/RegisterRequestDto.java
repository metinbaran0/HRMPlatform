package org.hrmplatform.hrmplatform.dto.request;

import jakarta.validation.constraints.*;


public record RegisterRequestDto(
		@Email(message = "Email geçerli formatta değil.")
		@NotBlank(message = "Email boş geçilemez.")
		String email,
		
		@Size(min = 8, max = 50, message = "Password için en az 8 karakter girmelisiniz.")
		@NotBlank
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%*^&+=]).{8,20}$",
				message = "Şifre kurallara uygun değil.")
		String password,
		@NotBlank(message = "Şifre onayı boş olamaz.")
		String rePassword
) {}