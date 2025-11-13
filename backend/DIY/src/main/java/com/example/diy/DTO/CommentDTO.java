package com.example.diy.DTO;

import java.time.LocalDate;

public class CommentDTO {
    private Long id;
    private String content;
    private LocalDate createdAt;
    private UsersSimpleDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public UsersSimpleDTO getUser() {
        return user;
    }

    public void setUser(UsersSimpleDTO user) {
        this.user = user;
    }
}
