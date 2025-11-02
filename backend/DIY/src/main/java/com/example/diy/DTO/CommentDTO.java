package com.example.diy.DTO;

import java.time.LocalDate;

public class CommentDTO {
    private Long id;
    private String content;
    private LocalDate createdDate;
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

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public UsersSimpleDTO getUser() {
        return user;
    }

    public void setUser(UsersSimpleDTO user) {
        this.user = user;
    }
}
