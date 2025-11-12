package com.example.diy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
public class Project {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @ManyToOne
    private Users users;
    @ManyToOne
    private Category category;
    @ManyToOne(optional = true)
    private Challenge challenge;//לבדוק איך לעשות שיוכלו לעשות קשר של Null

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private List<Step> steps;

    @JsonIgnore
    @OneToMany(mappedBy = "project")
    private List<Comment> comments;

    @ManyToMany
    private Set<Tag> tags; // שינינו מ-List ל-Set
    @ManyToMany
    private List<Users> likedByUsers;

    @ManyToMany(mappedBy = "favoriteProjects")
    private List<Users> favoritedByUsers;

    @CreationTimestamp
    private LocalDate createdAt;
    //לבדוק איך להגדיל את הטקסט
    @Lob//@Column(length = 5000)
    @Column(columnDefinition = "TEXT")
    private String materials;
    private String title;
    private String ages;
    private String timePrep;
    private String picturePath;
    private String description;
    private boolean isDraft;

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picture) {
        this.picturePath = picture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }



    public List<Users> getFavoritedByUsers() {
        return favoritedByUsers;
    }

    public void setFavoritedByUsers(List<Users> favoritedByUsers) {
        this.favoritedByUsers = favoritedByUsers;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<Users> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(List<Users> likedByUsers) {
        this.likedByUsers = likedByUsers;
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
}
