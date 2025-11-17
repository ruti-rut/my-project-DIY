package com.example.diy.DTO;

import com.example.diy.model.AuthProvider;

public class UserResponseDTO {
    private Long id;
    private String userName;
    private String mail;
    private String city;
    private String aboutMe;
    private String profilePicturePath;
    private AuthProvider provider;
    private boolean isSubscribedToDaily;


    public boolean isSubscribedToDaily() {
        return isSubscribedToDaily;
    }

    public void setSubscribedToDaily(boolean subscribedToDaily) {
        isSubscribedToDaily = subscribedToDaily;
    }

    public UserResponseDTO() {
    }

    public UserResponseDTO(Long id, String userName, String mail, String city, String aboutMe, String profilePicturePath, AuthProvider provider) {
        this.id = id;
        this.userName = userName;
        this.mail = mail;
        this.city = city;
        this.aboutMe = aboutMe;
        this.profilePicturePath = profilePicturePath;
        this.provider = provider;
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

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }
}
