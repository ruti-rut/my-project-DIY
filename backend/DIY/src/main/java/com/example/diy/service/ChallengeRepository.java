package com.example.diy.service;

import com.example.diy.model.Challenge;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @EntityGraph(attributePaths = {"projects", "projects.users"})
    Optional<Challenge> findById(Long id);

    List<Challenge> findTop6ByOrderByEndDateAsc();
    @Query("SELECT c FROM Challenge c WHERE c.endDate >= CURRENT_DATE ORDER BY c.startDate DESC")
    List<Challenge> findTop3ActiveChallenges();

    /**
     * כל האתגרים הפעילים
     */
    @Query("SELECT c FROM Challenge c WHERE c.endDate >= :today ORDER BY c.startDate DESC")
    List<Challenge> findAllActiveChallenges(LocalDate today);

    /**
     * אתגרים שמסתיימים בקרוב (7 ימים הקדימה)
     */
    @Query("SELECT c FROM Challenge c WHERE c.endDate BETWEEN CURRENT_DATE AND :weekFromNow ORDER BY c.endDate ASC")
    List<Challenge> findChallengesEndingSoon(LocalDate weekFromNow);

    @Query("SELECT c FROM Challenge c LEFT JOIN FETCH c.projects p LEFT JOIN FETCH p.users u WHERE c.id = :id")
    Optional<Challenge> findByIdWithProjectsAndUsers(@Param("id") Long id);
}