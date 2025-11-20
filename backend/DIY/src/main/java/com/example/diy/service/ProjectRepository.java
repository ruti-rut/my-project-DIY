package com.example.diy.service;

import com.example.diy.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCategoryId(Long categoryId);

    @Query("SELECT p FROM Project p WHERE p.createdAt >= :yesterday")
    List<Project> findProjectsFromLast24Hours(@Param("yesterday") LocalDateTime yesterday);

    //    List<Project> findTop3ByCategoryIdOrderByCreatedDateDesc(Long categoryId);
    Page<Project> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
