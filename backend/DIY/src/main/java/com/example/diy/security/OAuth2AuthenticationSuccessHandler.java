package com.example.diy.security;

import com.example.diy.model.Users;
import com.example.diy.security.jwt.JwtUtils;
import com.example.diy.service.UsersRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. קבלת פרטי המשתמש מ-OAuth2
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 2. קבלת האימייל מהמידע שהתקבל מגוגל
        String email = oAuth2User.getAttribute("email");

        // 3. איתור המשתמש ב-DB באמצעות האימייל
        Optional<Users> userOptional = usersRepository.findByMail(email);

        Users user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // ** המשתמש לא קיים ב-DB - מאחר שאינך רוצה הרשמה, נסרב לכניסה
            // ניתן להפנות לדף שגיאה או לדף התחברות עם הודעה מתאימה
            System.out.println("User with email " + email + " not found in database. Login denied.");
            String redirectUrl = "http://localhost:4200/login?error=user_not_found"; // שנה לכתובת ה-FE שלך
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            return;
        }

        // 4. אם המשתמש נמצא - הנפקת JWT ו-Cookie
        CustomUserDetails userDetails = new CustomUserDetails(user);
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        // 5. הוספת ה-Cookie לתגובה
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        // 6. הפנייה חזרה ל-Frontend לאחר כניסה מוצלחת
        String targetUrl = "http://localhost:4200/dashboard";
        // שנה לכתובת המתאימה ב-Angular שלך
        this.setDefaultTargetUrl(targetUrl);
        // מבצע את ההפניה באמצעות הלוגיקה המובנית של Spring Security
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
