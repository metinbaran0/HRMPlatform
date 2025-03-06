package org.hrmplatform.hrmplatform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
		@NotBlank(message = "Yorum boş geçilemez.")
		String content
) {
}