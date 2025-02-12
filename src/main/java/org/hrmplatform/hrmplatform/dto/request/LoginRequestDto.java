package org.hrmplatform.hrmplatform.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public record LoginRequestDto (
		@NotBlank(message = "Email boş geçilemez.")
		String email,
		
		@NotBlank(message = "Sifre boş geçilemez.")
		
		String password
) {}