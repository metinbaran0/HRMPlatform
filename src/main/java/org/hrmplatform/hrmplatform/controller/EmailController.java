package org.hrmplatform.hrmplatform.controller;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.EmailRequest;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(EMAIL)
@CrossOrigin("*")
@RequiredArgsConstructor
public class EmailController {
	private final EmailService emailService;
	
	@PostMapping(SEND_EMAIL)
	public ResponseEntity<BaseResponse<Boolean>> sendEmail(@RequestBody EmailRequest emailRequest) {
		try {
			emailService.sendEmail(emailRequest);
			return ResponseEntity.ok(BaseResponse.<Boolean>builder()
			                                     .code(200)
			                                     .message("E-posta başarıyla gönderildi")
			                                     .success(true)
			                                     .data(true)
			                                     .build());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			                     .body(BaseResponse.<Boolean>builder()
			                                       .code(500)
			                                       .message("E-posta gönderme sırasında hata oluştu: " + e.getMessage())
			                                       .success(false)
			                                       .data(false)
			                                       .build());
		}
	}
}