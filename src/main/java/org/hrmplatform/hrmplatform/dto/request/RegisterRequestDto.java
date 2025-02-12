package org.hrmplatform.hrmplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record RegisterRequestDto(
		@Email(message = "Email geçerli formatta değil.")
		@NotBlank(message = "Email boş geçilemez.")
		String email,
		
		@Size(min = 8, max = 50, message = "Password için en az 8 karakter girmelisiniz.")
		@NotBlank
		@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%*^&+=]).{8,20}$",
				message = "Şifre kurallara uygun değil.")
		String password,
		
		String rePassword
) {}