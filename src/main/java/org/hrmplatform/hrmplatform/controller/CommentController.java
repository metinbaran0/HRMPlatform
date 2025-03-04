package org.hrmplatform.hrmplatform.controller;

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
	
	
	@PostMapping(CREATE_COMMENT)
	public ResponseEntity<BaseResponse<Comment>> addComment(@RequestBody String content) {
		Comment savedComment = commentService.addComment(content);
		
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
	public ResponseEntity<BaseResponse<List<Comment>>> getAllComments() {
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