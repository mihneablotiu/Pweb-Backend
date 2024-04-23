package com.pweb.springBackend.repositories;

import com.pweb.springBackend.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsTagByName(String name);
    Optional<Tag> findTagByName(String name);
}
