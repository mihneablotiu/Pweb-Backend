package com.pweb.springBackend.repositories;

import com.pweb.springBackend.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
