package org.hrmplatform.hrmplatform.repository;

import org.hrmplatform.hrmplatform.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}