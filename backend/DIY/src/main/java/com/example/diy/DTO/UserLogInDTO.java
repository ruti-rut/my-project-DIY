package com.example.diy.DTO;

import jakarta.validation.constraints.NotBlank;

public class UserLogInDTO {
    @NotBlank(message = "שם משתמש או דואר אלקטרוני נדרשים.")
    private String identifier; // נשתמש ב'identifier' כדי לכסות גם שם משתמש וגם מייל

    // סיסמה - חובה. אין צורך לבדוק אורך מינימלי כמו בהרשמה,
    // אך נשתמש ב-@NotBlank כדי לוודא שהסיסמה נשלחה.
    @NotBlank(message = "סיסמה נדרשת.")
    private String password;

    public @NotBlank(message = "שם משתמש או דואר אלקטרוני נדרשים.") String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(@NotBlank(message = "שם משתמש או דואר אלקטרוני נדרשים.") String identifier) {
        this.identifier = identifier;
    }

    public @NotBlank(message = "סיסמה נדרשת.") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "סיסמה נדרשת.") String password) {
        this.password = password;
    }
}
