package com.example.diy.DTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ProjectResponseDTO {
    private Long id;
    private UsersSimpleDTO users;       // DTO חלקי של המשתמש
    private CategoryDTO category;       // DTO חלקי של הקטגוריה

    private List<StepResponseDTO> steps; // **חייב להיות DTO של שלב**, לא Entity מלא!
    private List<CommentDTO> comments;   // **חייב להיות DTO של תגובה**
    private Set<TagDTO> tags;           // **חייב להיות DTO של תגית**

    // שדות פרימיטיביים
    private int likesCount;
    private LocalDate createdAt;
    private String materials;
    private String title;
    private String ages;
    private String timePrep;
    private String picture;
    private String description;
    private boolean isDraft;

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }

    // ...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersSimpleDTO getUsers() {
        return users;
    }

    public void setUsers(UsersSimpleDTO users) {
        this.users = users;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<StepResponseDTO> getSteps() {
        return steps;
    }

    public void setSteps(List<StepResponseDTO> steps) {
        this.steps = steps;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
