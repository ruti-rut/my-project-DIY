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

    @Column(name = "user_email") // <-- התיקון הקריטי למילה שמורה
    private String mail;
    // --- שדות OAuth2 ---
    // קובע אם המשתמש נרשם מקומית או דרך ספק חיצוני
    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;
    @JsonIgnore
    @OneToMany(mappedBy = "users")
    private List<Project> myProjects;
    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Comment> myComments;
    @JsonIgnore
    @ManyToMany
    private Set<Project> favoriteProjects;
    @JsonIgnore
    @ManyToMany(mappedBy = "likedByUsers")
    private Set<Project> likeProjects;
    private String city;
    private String aboutMe;
    private String profilePicturePath;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles = new HashSet<>();
    private boolean isSubscribedToDaily = false;
    private boolean emailVerified = false;
    private String verificationToken;

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public boolean isSubscribedToDaily() {
        return isSubscribedToDaily;
    }

    public void setSubscribedToDaily(boolean subscribedToDaily) {
        isSubscribedToDaily = subscribedToDaily;
    }

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

    public Set<Project> getFavoriteProjects() {
        return favoriteProjects;
    }

    public void setFavoriteProjects(Set<Project> favoriteProjects) {
        this.favoriteProjects = favoriteProjects;
    }

    public Set<Project> getLikeProjects() {
        return likeProjects;
    }

    public void setLikeProjects(Set<Project> likeProjects) {
        this.likeProjects = likeProjects;
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
