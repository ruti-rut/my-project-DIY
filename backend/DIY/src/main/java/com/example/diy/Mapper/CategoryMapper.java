package com.example.diy.Mapper;

import com.example.diy.DTO.CategoryDTO;
import com.example.diy.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface CategoryMapper {
    default CategoryDTO categoryToDTO(Category category){
        CategoryDTO categoryDTO=new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        return categoryDTO;
    }
}

