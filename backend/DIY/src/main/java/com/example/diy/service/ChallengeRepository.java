package com.example.diy.service;

import com.example.diy.model.Challenge;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @EntityGraph(attributePaths = {"projects", "projects.users"})
    Optional<Challenge> findById(Long id);
}