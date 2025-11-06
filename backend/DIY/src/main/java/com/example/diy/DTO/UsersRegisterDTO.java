package com.example.diy.DTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsersRegisterDTO {
    @NotBlank(message = "שם משתמש הוא שדה חובה.")
    @Size(min = 4, max = 50, message = "שם המשתמש חייב להיות בין 4 ל-50 תווים.")
    private String userName;

    // 2. וולידציה לסיסמה
    @NotBlank(message = "סיסמה היא שדה חובה.")
    // חשוב: לוודא אורך מינימלי סביר לחוזק
    @Size(min = 8, message = "הסיסמה חייבת להיות באורך של 8 תווים לפחות.")
    private String password;

    // 3. וולידציה למייל
    @NotBlank(message = "דואר אלקטרוני הוא שדה חובה.")
    @Email(message = "פורמט הדואר האלקטרוני אינו תקין.")
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
