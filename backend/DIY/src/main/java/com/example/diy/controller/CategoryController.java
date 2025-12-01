package com.example.diy.controller;

import com.example.diy.DTO.CategoryDTO;
import com.example.diy.Mapper.CategoryMapper;
import com.example.diy.model.Category;
import com.example.diy.service.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")

public class CategoryController {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    public CategoryController(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/allCategories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        try {
            List<Category> list = categoryRepository.findAll();

            if (list == null || list.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(categoryMapper.CategoryToCategoryDTO(list));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}