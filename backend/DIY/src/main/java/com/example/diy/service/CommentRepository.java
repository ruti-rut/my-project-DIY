package com.example.diy.service;

import com.example.diy.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    Page<Comment> findByProjectIdOrderByCreatedAtDesc(
            Long projectId,
            Pageable pageable);
}

