package com.pweb.springBackend.repositories;

import com.pweb.springBackend.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
