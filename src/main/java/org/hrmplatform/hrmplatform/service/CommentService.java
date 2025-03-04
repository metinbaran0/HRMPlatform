package org.hrmplatform.hrmplatform.service;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.entity.Comment;
import org.hrmplatform.hrmplatform.repository.CommentRepository;
import org.hrmplatform.hrmplatform.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final UserRoleRepository userRoleRepository;
	
	public Comment addComment(String content) {
		if (content == null || content.trim().isEmpty()) {
			throw new IllegalArgumentException("Yorum içeriği boş olamaz!");
		}
		
		Comment comment = new Comment();
		comment.setContent(content);
		comment.setCreatedAt(LocalDateTime.now());
		
		return commentRepository.save(comment);
	}
	
	public List<Comment> getAllComments() {
		return commentRepository.findAll();
	}
	
}