package com.example.diy.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StepDTO {
    @NotNull(message = "Step number is mandatory")
    @Min(value = 1, message = "Step number must be 1 or higher")
    @Max(value = 99, message = "Step number cannot exceed 99")
    private Integer stepNumber;

    @NotNull(message = "Title is mandatory")
    @Size(min=3, max=150, message="Title must be between 3 and 150 characters long")
    private String title;

    @NotNull(message = "Content is mandatory")
    @Size(min=10, max=2000, message="Content must be between 10 and 2000 characters long")
    private String content;

    @NotNull(message = "Project ID is mandatory for a step")
    private Long ProjectId;

    private String picturePath;

    public void setProjectId(Long projectId) {
        ProjectId = projectId;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Long getProjectId() {
        return ProjectId;
    }

    public void setProjectId(long projectId) {
        ProjectId = projectId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
