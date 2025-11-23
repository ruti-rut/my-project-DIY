package com.example.diy.service;

import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.model.Project;
import com.example.diy.model.Tag;
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

    List<Project> findTop3ByCategoryIdOrderByCreatedAtDesc(Long categoryId);
    Page<Project> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN p.tags t " +
            "WHERE UPPER(p.title) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
            "OR UPPER(t.name) LIKE UPPER(CONCAT('%', :searchTerm, '%'))")
    Page<Project> searchByTitleOrTags(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<Project> findAllByOrderByCreatedAtAsc(Pageable pageable);

    @Query(value = "SELECT p FROM Project p " +
            "LEFT JOIN p.likedByUsers u " +
            "GROUP BY p " +
            "ORDER BY COUNT(u) DESC",
            // הוספת שאילתת ספירה מותאמת אישית
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM Project p")
    Page<Project> findAllOrderByLikesCountDesc(Pageable pageable);}
