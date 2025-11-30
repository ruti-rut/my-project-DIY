package com.example.diy.DTO;

import jakarta.validation.constraints.Size;

import java.util.List;

public class ProjectCreateDTO {
    @Size(min=3, max=20, message="Title should be between 3 to 20 characters Long")
    private String title;
    private String description;
    private String materials;
    private Long categoryId;
    private Long challengeId;
    private String ages;
    private String timePrep;
    private boolean isDraft;
    private List<String> tagNames;

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }


    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public Long getCategoryId() {
        return categoryId;
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
