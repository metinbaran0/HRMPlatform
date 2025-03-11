package org.hrmplatform.hrmplatform.controller;

import org.hrmplatform.hrmplatform.dto.request.CommentRequestDto;
import org.hrmplatform.hrmplatform.dto.response.BaseResponse;
import org.hrmplatform.hrmplatform.entity.Comment;
import org.hrmplatform.hrmplatform.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.hrmplatform.hrmplatform.constant.EndPoints.*;

@RestController
@RequestMapping(COMMENT)
@CrossOrigin("*")
public class CommentController {
	private final CommentService commentService;
	
	public CommentController(CommentService commentService) {
		this.commentService = commentService;
	}
	
	// Yeni eklenen public yorumları almak için endpoint
	@GetMapping("/v1/api/public/comments")  // Public endpoint
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
	
	@PostMapping(CREATE_COMMENT)
	public ResponseEntity<BaseResponse<Comment>> addComment(@RequestBody CommentRequestDto request) {
		if (request == null || request.content() == null || request.content().trim().isEmpty()) {
			return ResponseEntity.badRequest().body(
					BaseResponse.<Comment>builder()
					            .code(400)
					            .success(false)
					            .message("Yorum içeriği boş olamaz!")
					            .build()
			);
		}
		
		Comment savedComment = commentService.addComment(request);
		
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