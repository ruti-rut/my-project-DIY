package com.example.diy.DTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsersRegisterDTO {
    @NotBlank(message = "Username is a required field ")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters long")
    private String userName;

    // 2. וולידציה לסיסמה
    @NotBlank(message = "Password is a required field")
    // חשוב: לוודא אורך מינימלי סביר לחוזק
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // 3. וולידציה למייל
    @NotBlank(message = "Email is a required field.")
    @Email(message = "Invalid email format")
    private String mail;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
