package org.hrmplatform.hrmplatform.service;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CommentRequestDto;
import org.hrmplatform.hrmplatform.entity.Comment;
import org.hrmplatform.hrmplatform.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	
	public Comment addComment(CommentRequestDto request) {
		if (request.content() == null || request.content().trim().isEmpty()) {
			throw new IllegalArgumentException("Yorum içeriği boş olamaz!");
		}
		
		Comment comment = new Comment();
		comment.setContent(request.content());
		comment.setAuthor(request.author());
		comment.setAuthorImage(request.authorImage());
		comment.setPosition(request.position());
		comment.setCompany(request.company());
		comment.setCreatedAt(LocalDateTime.now());
		comment.setUpdatedAt(LocalDateTime.now());
		
		return commentRepository.save(comment);
	}
	
	public List<Comment> getAllComments() {
		return commentRepository.findAll();
	}
}