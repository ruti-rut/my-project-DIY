package com.example.diy.service;

import com.example.diy.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    List<Project> findByCategoryId(Long categoryId);
    List<Project> findTop3ByCategoryIdOrderByCreatedDateDesc(Long categoryId);


}
