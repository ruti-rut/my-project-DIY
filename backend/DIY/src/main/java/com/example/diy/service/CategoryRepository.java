package com.example.diy.service;

import com.example.diy.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;

public interface CategoryRepository extends JpaRepository<Category,Long> {


}
