package org.hrmplatform.hrmplatform.service;

import lombok.RequiredArgsConstructor;
import org.hrmplatform.hrmplatform.dto.request.CommentRequestDto;
import org.hrmplatform.hrmplatform.entity.Comment;
import org.hrmplatform.hrmplatform.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.hrmplatform.hrmplatform.entity.Company;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	
	// Şirket için zaten bir yorum olup olmadığını kontrol eden metod
	public boolean existsByCompanyId(Long companyId) {
		return commentRepository.existsByCompany_Id(companyId); // Doğru ilişki üzerinden kontrol
	}
	
	// Yalnızca bir kere yorum eklenmesini sağlayan güncellenmiş metod
	public Comment addComment(CommentRequestDto request, Long companyId) {
		if (request.content() == null || request.content().trim().isEmpty()) {
			throw new IllegalArgumentException("Yorum içeriği boş olamaz!");
		}
		
		// Şirket için zaten bir yorum eklenmiş mi kontrol et
		if (existsByCompanyId(companyId)) {
			throw new IllegalStateException("Bu şirket için zaten bir yorum eklenmiş!");
		}
		
		Comment comment = new Comment();
		comment.setContent(request.content());
		comment.setAuthor(request.author());
		comment.setAuthorImage(request.authorImage());
		comment.setPosition(request.position());
		
		// Şirket ile ilişkiyi kurmak
		Company company = new Company(); // Burada Company entity'sini yüklemelisiniz.
		company.setId(companyId); // Şirket ID'sini burada kullanabilirsiniz
		comment.setCompany(company);
		
		comment.setCreatedAt(LocalDateTime.now());
		comment.setUpdatedAt(LocalDateTime.now());
		
		return commentRepository.save(comment);
	}
	
	// Tüm yorumları getiren metod
	public List<Comment> getAllComments() {
		return commentRepository.findAll();
	}
}