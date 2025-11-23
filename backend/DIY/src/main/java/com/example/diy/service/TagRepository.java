package com.example.diy.service;

import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag,Long> {
    List<Tag> findByNameIn(List<String> names);
}
