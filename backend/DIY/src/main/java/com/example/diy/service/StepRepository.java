package com.example.diy.service;

import com.example.diy.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StepRepository extends JpaRepository<Step,Long> {
    List<Step> findByProjectId(Long id);
    @Modifying
    @Transactional
    void deleteByProjectId(Long projectId);

}
