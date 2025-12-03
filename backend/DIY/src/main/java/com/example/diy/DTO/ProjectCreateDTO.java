package com.example.diy.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ProjectCreateDTO {
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters long")
    @NotNull(message = "Title is mandatory")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @NotNull(message = "Description is mandatory")
    private String description;

    @Size(max = 500, message = "Materials list cannot exceed 500 characters")
    @NotNull(message = "Materials are mandatory")
    private String materials;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;

    private Long challengeId;

    @Size(max = 50, message = "Ages information cannot exceed 50 characters")
    @NotNull(message = "Ages information is mandatory")
    private String ages;

    @Size(max = 50, message = "Time prep information cannot exceed 50 characters")
    @NotNull(message = "Time preparation information is mandatory")
    private String timePrep;

    private boolean isDraft;

    @Size(max = 10, message = "Maximum 10 tags allowed")
    @Valid
    private List<String> tagNames;

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getMaterials() {
        return materials;
    }

    public void setMaterials(String materials) {
        this.materials = materials;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAges() {
        return ages;
    }

    public void setAges(String ages) {
        this.ages = ages;
    }

    public String getTimePrep() {
        return timePrep;
    }

    public void setTimePrep(String timePrep) {
        this.timePrep = timePrep;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }
}
