package org.hrmplatform.hrmplatform.controller;

import org.hrmplatform.hrmplatform.dto.request.CommentRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Comment;
import org.hrmplatform.hrmplatform.service.AuthService;
import org.hrmplatform.hrmplatform.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(COMMENT)
@CrossOrigin("*")
public class CommentController {
	private final CommentService commentService;
	private final AuthService authService;
	
	public CommentController(CommentService commentService, AuthService authService) {
		this.commentService = commentService;
		this.authService = authService;
	}
	
	@GetMapping("/comments")  // Public endpoint
	public ResponseEntity<BaseResponse<List<Comment>>> getPublicComments(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		List<Comment> comments = commentService.getAllComments();
		return ResponseEntity.ok(
				BaseResponse.<List<Comment>>builder()
				            .code(200)
				            .success(true)
				            .data(comments)
				            .message("Tüm yorumlar başarıyla getirildi.")
				            .build()
		);
	}
	
	@PreAuthorize("hasAuthority('COMPANY_ADMIN')") // Sadece COMPANY_ADMIN yetkisi olanlar erişebilir
	@PostMapping(CREATE_COMMENT)
	public ResponseEntity<BaseResponse<Comment>> addComment(
			@RequestHeader("Authorization") String token,
			@RequestBody CommentRequestDto request) {
		
		// Token doğrulama
		if (token == null || !token.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
					BaseResponse.<Comment>builder()
					            .code(401)
					            .success(false)
					            .message("Yetkisiz erişim: Geçerli bir token gereklidir.")
					            .build()
			);
		}
		
		// Token'dan yöneticinin şirket ID'sini al
		Long companyId = authService.getCompanyIdFromToken(token);
		if (companyId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
					BaseResponse.<Comment>builder()
					            .code(401)
					            .success(false)
					            .message("Geçersiz token: Kullanıcı doğrulanamadı veya şirket bilgisi bulunamadı.")
					            .build()
			);
		}
		
		// Boş içerik kontrolü
		if (request == null || request.content() == null || request.content().trim().isEmpty()) {
			return ResponseEntity.badRequest().body(
					BaseResponse.<Comment>builder()
					            .code(400)
					            .success(false)
					            .message("Yorum içeriği boş olamaz!")
					            .build()
			);
		}
		
		// Şirketin zaten bir yorumu olup olmadığını kontrol et
		boolean commentExists = commentService.existsByCompanyId(companyId);
		if (commentExists) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					BaseResponse.<Comment>builder()
					            .code(400)
					            .success(false)
					            .message("Bu şirket için zaten bir yorum eklenmiş. Yeni yorum eklenemez.")
					            .build()
			);
		}
		
		// Yorumu ekle (Şirket ID'si ile birlikte)
		Comment savedComment = commentService.addComment(request, companyId);
		
		return ResponseEntity.ok(
				BaseResponse.<Comment>builder()
				            .code(200)
				            .success(true)
				            .data(savedComment)
				            .message("Yorum başarıyla eklendi.")
				            .build()
		);
	}
	
	@GetMapping(GETALL_COMMENT)
	public ResponseEntity<BaseResponse<List<Comment>>> getAllComments(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size
	) {
		List<Comment> comments = commentService.getAllComments();
		return ResponseEntity.ok(
				BaseResponse.<List<Comment>>builder()
				            .code(200)
				            .success(true)
				            .data(comments)
				            .message("Tüm yorumlar başarıyla getirildi.")
				            .build()
		);
	}
}