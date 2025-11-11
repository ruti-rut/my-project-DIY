package com.example.diy.Mapper;

import com.example.diy.DTO.CategoryDTO;
import com.example.diy.model.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")

public interface CategoryMapper {
    List<CategoryDTO> CategoryToCategoryDTO(List<Category> category);

}

