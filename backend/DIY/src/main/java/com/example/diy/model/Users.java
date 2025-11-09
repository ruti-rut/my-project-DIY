package com.example.diy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Users {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    private String password;
    private String userName;
    private String mail;
    // --- שדות OAuth2 ---
    // קובע אם המשתמש נרשם מקומית או דרך ספק חיצוני
    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;
    @JsonIgnore
    @OneToMany(mappedBy = "users")
    private List<Project> myProjects;
    @JsonIgnore
    @OneToMany(mappedBy = "users")
    private List<Comment> myComments;
    @ManyToMany
    private List<Project> favoriteProjects;
    @ManyToMany(mappedBy = "likedByUsers")
    private List<Project> likeProjects;
    private String city;
    private String aboutMe;
    private String profilePicturePath;
    @ManyToMany
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<Project> getLikeProjects() {
        return likeProjects;
    }

    public void setLikeProjects(List<Project> likeProjects) {
        this.likeProjects = likeProjects;
    }

    public List<Project> getFavoriteProjects() {
        return favoriteProjects;
    }

    public void setFavoriteProjects(List<Project> favoriteProjects) {
        this.favoriteProjects = favoriteProjects;
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

    public List<Project> getMyProjects() {
        return myProjects;
    }

    public void setMyProjects(List<Project> myProjects) {
        this.myProjects = myProjects;
    }

    public String getProfilePicturePath() {
        return profilePicturePath;
    }

    public void setProfilePicturePath(String profilePicture) {
        this.profilePicturePath = profilePicture;
    }

    public List<Comment> getMyComments() {
        return myComments;
    }

    public void setMyComments(List<Comment> myComments) {
        this.myComments = myComments;
    }

}
