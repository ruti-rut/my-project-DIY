package com.example.diy.DTO;

import java.util.List;
import java.util.Map;

public class HomeResponseDTO {

    // מפת הפרויקטים לפי קטגוריות
    private Map<Long, List<ProjectListDTO>> projectsPerCategory;

    // רשימת האתגרים שנסגרים בקרוב
    private List<ChallengeListDTO> latestChallenges;

    public HomeResponseDTO(Map<Long, List<ProjectListDTO>> projectsPerCategory, List<ChallengeListDTO> latestChallenges) {
        this.projectsPerCategory = projectsPerCategory;
        this.latestChallenges = latestChallenges;
    }

    // Getters and Setters...
    public Map<Long, List<ProjectListDTO>> getProjectsPerCategory() {
        return projectsPerCategory;
    }

    public void setProjectsPerCategory(Map<Long, List<ProjectListDTO>> projectsPerCategory) {
        this.projectsPerCategory = projectsPerCategory;
    }

    public List<ChallengeListDTO> getLatestChallenges() {
        return latestChallenges;
    }

    public void setLatestChallenges(List<ChallengeListDTO> latestChallenges) {
        this.latestChallenges = latestChallenges;
    }
}