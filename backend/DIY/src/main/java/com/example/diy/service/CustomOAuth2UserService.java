package com.example.diy.service;


import com.example.diy.model.AuthProvider;
import com.example.diy.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsersRepository usersRepository; // לגישה לבסיס הנתונים (DB)

    // המתודה ש-Spring Security קורא לה כדי לטעון משתמש מגוגל
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 1. טעינת פרטי המשתמש הבסיסיים מגוגל
        OAuth2User oauth2User = super.loadUser(userRequest);

        // 2. שליפת הפרטים הקריטיים (מייל ושם)
        String mail = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        // 3. חיפוש המשתמש בבסיס הנתונים המקומי שלנו
        Optional<Users> userOptional = usersRepository.findByMail(mail);
        Users user;

        if (userOptional.isEmpty()) {
            // *** תרחיש 1: רישום חדש (SignUp) ***
            // המשתמש לא קיים ב-DB, יוצרים אותו כמשתמש GOOGLE
            user = registerNewOAuth2User(mail, name);
        } else {
            // *** תרחיש 2: כניסה (Login) או שדרוג ***
            user = userOptional.get();

            // בדיקה: אם המשתמש קיים אבל נרשם מקומית (LOCAL), נשדרג אותו
            if (user.getProvider() == AuthProvider.LOCAL) {
                user = updateExistingOAuth2User(user);
            }
            // אם הוא כבר GOOGLE, ממשיכים כרגיל (Login)
        }

        // 4. החזרת אובייקט ה-OAuth2User (שכעת מקושר ל-DB שלנו)
        return oauth2User;
    }

    // *** לוגיקת רישום משתמש חדש מ-OAuth2 ***
    private Users registerNewOAuth2User(String email, String name) {
        Users newUser = new Users();
        newUser.setMail(email);
        newUser.setUserName(name);
        newUser.setProvider(AuthProvider.GOOGLE); // הגדרה כמשתמש גוגל

        // יצירת סיסמה אקראית מוצפנת. לא משתמשים בה, אבל שדה הסיסמה אינו ריק.
        // אנו מניחים ש-passwordEncoder() הוזרק כ-Bean.
        newUser.setPassword(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()));

        return usersRepository.save(newUser);
    }

    // *** לוגיקת עדכון משתמש קיים ***
    private Users updateExistingOAuth2User(Users existingUser) {
        existingUser.setProvider(AuthProvider.GOOGLE);
        // ניתן לעדכן פרטים נוספים אם גוגל סיפקה מידע חדש
        return usersRepository.save(existingUser);
    }
}