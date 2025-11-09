package com.example.diy.security;

import com.example.diy.model.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends User {

    // 1. הוספת השדה לאובייקט המשתמש המלא
    private final Users user;

    // 2. עדכון הבנאי כך שיקבל את אובייקט ה-Users
    public CustomUserDetails(Users user) {
        // קורא לבנאי של מחלקת האב (Spring Security User)
        super(user.getUserName(),
                user.getPassword(),
                buildAuthorities(user));
        this.user = user;
    }

    // מתודת עזר לבניית רשימת הרשויות מתוך המודל שלך (בהנחה שיש לך שדה 'roles')
    // אם אין לך שדה roles במודל, התאם את הפונקציה הזו
    private static Collection<? extends GrantedAuthority> buildAuthorities(Users user) {
        // כאן אתה צריך להתאים את הלוגיקה לאופן שבו אתה מאחסן תפקידים (Roles)
        // דוגמה: אם אתה משתמש בשדה יחיד או קבוע עבור כל משתמש
        String role = "ROLE_USER"; // דוגמה, אם יש לך שדה תפקיד, השתמש בו
        return List.of(new SimpleGrantedAuthority(role));
    }

    // 3. המתודה החסרה: מאפשרת לגשת לאובייקט המשתמש המלא
    public Users getUser() {
        return user;
    }

    // אופציונלי: הוספת מתודות גישה נוחות לשדות ספציפיים
    public Long getId() {
        return user.getId();
    }
}