package com.example.diy.DTO;

import java.time.LocalDate;
import java.util.List;

public class ChallengeResponseDTO {
    Long id;
    String theme;
    String content;
    LocalDate startDate;
    LocalDate endDate;
    String picturePath;
    String picture;       // base64 – בדיוק כמו ב-ProjectListDTO
    List<ProjectListDTO> projects;  // ← ישירות את מה שכבר יש לך!
    String status;           // "OPEN" | "UPCOMING" | "CLOSED"

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<ProjectListDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectListDTO> projects) {
        this.projects = projects;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
