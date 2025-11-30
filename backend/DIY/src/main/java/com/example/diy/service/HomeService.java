package com.example.diy.service;

import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.Mapper.ProjectMapper;
import com.example.diy.model.Category;
import com.example.diy.model.Project;
import com.example.diy.model.Users;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class HomeService {

    CategoryRepository categoryRepository;
    ProjectRepository projectRepository;
    ProjectMapper projectMapper;

    public Map<Long, List<ProjectListDTO>> getLatestProjectsPerCategory(Users currentUser) {
        List<Category> categories = categoryRepository.findAll();
        Map<Long, List<ProjectListDTO>> result = new HashMap<>();

        for (Category cat : categories) {
            // שולף את 3 הפרויקטים האחרונים של הקטגוריה
            List<Project> latestProjects = projectRepository
                    .findTop3ByCategoryIdOrderByCreatedAtDesc(cat.getId());

            // ממפה ל-DTO
            List<ProjectListDTO> dtoList = projectMapper.toProjectListDTOList(latestProjects, currentUser);
            // מוסיף למפה לפי קטגוריה
            result.put(cat.getId(), dtoList);
        }

        return result;
    }
}


