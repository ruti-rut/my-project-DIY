package com.example.diy.DTO;

//not projectCard?
public class ProjectListDTO {
    private Long id;
    private UsersSimpleDTO usersSimpleDTO;
    private String title;
    private String picture;
    private String picturePath;
    private Long challengeId;

    public long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(long challengeId) {
        this.challengeId = challengeId;
    }

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

    public UsersSimpleDTO getUsersSimpleDTO() {
        return usersSimpleDTO;
    }

    public void setUsersSimpleDTO(UsersSimpleDTO usersSimpleDTO) {
        this.usersSimpleDTO = usersSimpleDTO;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
