package com.example.diy.controller;

import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.model.Tag;
import com.example.diy.service.TagRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/tags") // הוספתי RequestMapping ברמת המחלקה לצורך קונבנציה טובה יותר
public class TagController {
    TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping("/allTags")
    public ResponseEntity<List<Tag>> getAllTags() {
        try {
            List<Tag> tags = tagRepository.findAll();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}