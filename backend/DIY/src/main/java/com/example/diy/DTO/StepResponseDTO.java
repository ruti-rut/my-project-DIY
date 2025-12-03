package com.example.diy.DTO;

//not stepToShow?
public class StepResponseDTO {
    private Long id; // נחוץ כדי לערוך שלב קיים בעתיד
    private int stepNumber;
    private String title;
    private String content;
    private String picture;
    private String picturePath; // ✅ הוסף שדה זה - הנתיב המקורי בשרת

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
