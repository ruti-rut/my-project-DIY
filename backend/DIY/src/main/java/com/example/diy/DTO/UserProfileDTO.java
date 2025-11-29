package com.example.diy.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserProfileDTO {
    private Long id;
    private String userName;
    private String mail;
    private String city;
    private String aboutMe;
    private String profilePicturePath;
    private LocalDateTime joinDate;
    private List<ProjectListDTO> myProjects = new ArrayList<>();
    private List<ProjectListDTO> favoriteProjects = new ArrayList<>();
    private int projectsCount;
    private int favoritesCount;

    public int getProjectsCount() {
        return projectsCount;
    }

    public void setProjectsCount(int projectsCount) {
        this.projectsCount = projectsCount;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public void setFavoritesCount(int favoritesCount) {
        this.favoritesCount = favoritesCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public List<ProjectListDTO> getMyProjects() {
        return myProjects;
    }

    public void setMyProjects(List<ProjectListDTO> myProjects) {
        this.myProjects = myProjects;
    }

    public List<ProjectListDTO> getFavoriteProjects() {
        return favoriteProjects;
    }

    public void setFavoriteProjects(List<ProjectListDTO> favoriteProjects) {
        this.favoriteProjects = favoriteProjects;
    }
}
