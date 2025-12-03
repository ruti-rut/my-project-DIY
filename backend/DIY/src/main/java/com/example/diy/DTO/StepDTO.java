package com.example.diy.DTO;

public class StepDTO {
    private int stepNumber;
    private String title;
    private String content;
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
