package com.example.diy.service;

import com.example.diy.model.Project;
import com.example.diy.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    List<Project> findByCategoryId(Long categoryId);

    List<Project> findByUsers(Users user);

    @Query("SELECT p FROM Project p WHERE p.createdAt >= :yesterday")
    List<Project> findProjectsFromLast24Hours(@Param("yesterday") LocalDateTime yesterday);

    List<Project> findTop6ByCategoryIdOrderByCreatedAtDesc(Long categoryId);


    @Query("SELECT p FROM Project p " +
            "LEFT JOIN p.tags t " +
            "LEFT JOIN p.likedByUsers u " +
            "WHERE (:searchTerm IS NULL OR :searchTerm = '' OR " +
            "(UPPER(p.title) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
            "OR UPPER(t.name) LIKE UPPER(CONCAT('%', :searchTerm, '%')))) " +
            "AND (:categoryIds IS NULL OR p.category.id IN :categoryIds) " + // תנאי קטגוריה אופציונלי
            "GROUP BY p.id " +
            "ORDER BY COUNT(u) DESC")
    Page<Project> findPopularProjects(
            @Param("searchTerm") String searchTerm,
            @Param("categoryIds") List<Long> categoryIds,
            Pageable pageable
    );

    List<Project> findTop3ByOrderByCreatedAtDesc();

    @Modifying
    @Query(value = "DELETE FROM USERS_FAVORITE_PROJECTS WHERE FAVORITE_PROJECTS_ID = :projectId", nativeQuery = true)
    void clearFavoriteProjectsJoinTable(@Param("projectId") Long projectId);

    @Modifying
    @Query(value = "DELETE FROM PROJECT_LIKED_BY_USERS WHERE LIKE_PROJECTS_ID = :projectId", nativeQuery = true)
    void clearLikedProjectsJoinTable(@Param("projectId") Long projectId);

    List<Project> findByTitleContainingIgnoreCase(String keyword);

    List<Project> findByDescriptionContainingIgnoreCase(String description);

    List<Project> findByTags_NameContainingIgnoreCase(String keyword);

    boolean existsByChallengeIdAndUsersId(Long challengeId, Long userId);

    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.users WHERE p.challenge.id = :challengeId")
    List<Project> findProjectsByChallengeIdWithUsers(@Param("challengeId") Long challengeId);


    //    // חיפוש לפי כותרת או תגיות
//    @Query("SELECT DISTINCT p FROM Project p " +
//            "LEFT JOIN p.tags t " +
//            "WHERE (:searchTerm IS NULL OR :searchTerm = '' OR " +
//            "LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
//            "LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
//    Page<Project> searchByTitleOrTags(@Param("searchTerm") String searchTerm, Pageable pageable);
//
//    Page<Project> findAllByOrderByCreatedAtAsc(Pageable pageable);
//
//    // הכל מיון לפי לייקס
//    @Query(value = "SELECT p FROM Project p " +
//            "LEFT JOIN p.likedByUsers u " +
//            "GROUP BY p " +
//            "ORDER BY COUNT(u) DESC",
//            countQuery = "SELECT COUNT(DISTINCT p.id) FROM Project p")
//    Page<Project> findAllOrderByLikesCountDesc(Pageable pageable);
//
//    // חיפוש + פופולריות (ללא קטגוריה)
//    @Query("SELECT DISTINCT p FROM Project p " +
//            "LEFT JOIN p.tags t " +
//            "LEFT JOIN p.likedByUsers u " +
//            "WHERE UPPER(p.title) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
//            "OR UPPER(t.name) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
//            "GROUP BY p " +
//            "ORDER BY COUNT(u) DESC")
//    Page<Project> searchByTitleOrTagsOrderByLikes(
//            @Param("searchTerm") String searchTerm,
//            Pageable pageable
//    );
//
//    // ============ קטגוריות מרובות ============
//
//    // קטגוריות מרובות - פשוט
//    @Query("SELECT p FROM Project p WHERE p.category.id IN :categoryIds")
//    Page<Project> findByCategoryIds(
//            @Param("categoryIds") List<Long> categoryIds,
//            Pageable pageable
//    );
//
//    // קטגוריות מרובות + פופולריות
//    @Query("SELECT p FROM Project p " +
//            "LEFT JOIN p.likedByUsers u " +
//            "WHERE p.category.id IN :categoryIds " +
//            "GROUP BY p " +
//            "ORDER BY COUNT(u) DESC")
//    Page<Project> findByCategoryIdsOrderByLikes(
//            @Param("categoryIds") List<Long> categoryIds,
//            Pageable pageable
//    );
//
//    // חיפוש + קטגוריות מרובות
//    @Query("SELECT DISTINCT p FROM Project p " +
//            "LEFT JOIN p.tags t " +
//            "WHERE (UPPER(p.title) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
//            "OR UPPER(t.name) LIKE UPPER(CONCAT('%', :searchTerm, '%'))) " +
//            "AND p.category.id IN :categoryIds")
//    Page<Project> searchByTitleOrTagsAndCategories(
//            @Param("searchTerm") String searchTerm,
//            @Param("categoryIds") List<Long> categoryIds,
//            Pageable pageable
//    );
//
//    // חיפוש + קטגוריות מרובות + פופולריות
//    @Query("SELECT DISTINCT p FROM Project p " +
//            "LEFT JOIN p.tags t " +
//            "LEFT JOIN p.likedByUsers u " +
//            "WHERE (UPPER(p.title) LIKE UPPER(CONCAT('%', :searchTerm, '%')) " +
//            "OR UPPER(t.name) LIKE UPPER(CONCAT('%', :searchTerm, '%'))) " +
//            "AND p.category.id IN :categoryIds " +
//            "GROUP BY p " +
//            "ORDER BY COUNT(u) DESC")
//    Page<Project> searchByTitleOrTagsAndCategoriesOrderByLikes( // **זו הפונקציה שנוספה לשימוש ב Controller**
//                                                                @Param("searchTerm") String searchTerm,
//                                                                @Param("categoryIds") List<Long> categoryIds,
//                                                                Pageable pageable
//    );



}