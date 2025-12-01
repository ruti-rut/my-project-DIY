package com.example.diy.service;

import com.example.diy.DTO.ChallengeListDTO;
import com.example.diy.DTO.HomeResponseDTO; // 1. ייבוא ה-DTO החדש
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.Mapper.ProjectMapper;
import com.example.diy.Mapper.ChallengeMapper; // 2. ייבוא ה-ChallengeMapper
import com.example.diy.model.Category;
import com.example.diy.model.Challenge; // 3. ייבוא ה-Challenge
import com.example.diy.model.Project;
import com.example.diy.model.Users;
import org.springframework.stereotype.Service;

import java.time.LocalDate; // 5. ייבוא LocalDate
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HomeService {

    CategoryRepository categoryRepository;
    ProjectRepository projectRepository;
    ChallengeRepository challengeRepository; // 6. הוספת ה-Repository של האתגרים
    ProjectMapper projectMapper;
    ChallengeMapper challengeMapper; // 7. הוספת ה-Mapper של האתגרים

    // 8. בנאי להזרקה (צריך להיות בנאי מלא)
    public HomeService(CategoryRepository categoryRepository,
                       ProjectRepository projectRepository,
                       ChallengeRepository challengeRepository,
                       ProjectMapper projectMapper,
                       ChallengeMapper challengeMapper) {
        this.categoryRepository = categoryRepository;
        this.projectRepository = projectRepository;
        this.challengeRepository = challengeRepository;
        this.projectMapper = projectMapper;
        this.challengeMapper = challengeMapper;
    }

    // 9. שינוי חתימת המתודה להחזרת HomeResponseDTO
    public HomeResponseDTO getHomeData(Users currentUser) {

        // --- א. שליפת הפרויקטים (כמו שהיה) ---

        List<Category> categories = categoryRepository.findAll();
        Map<Long, List<ProjectListDTO>> projectsMap = new HashMap<>();

        for (Category cat : categories) {
            List<Project> latestProjects = projectRepository
                    .findTop6ByCategoryIdOrderByCreatedAtDesc(cat.getId());

            List<ProjectListDTO> dtoList = projectMapper.toProjectListDTOList(latestProjects, currentUser);
            projectsMap.put(cat.getId(), dtoList);
        }

        // --- ב. שליפת האתגרים (הלוגיקה החדשה) ---

        // שליפת האתגרים הפתוחים שנסגרים הכי בקרוב
        List<Challenge> challenges = challengeRepository
                .findTop6ByEndDateAfterOrderByEndDateAsc(LocalDate.now());

        // מיפוי ל-DTO
        List<ChallengeListDTO> challengeDtoList = challengeMapper.toChallengeListDTOList(challenges);

        // --- ג. החזרת התגובה המאוחדת --
        return new HomeResponseDTO(projectsMap, challengeDtoList);
    }
}