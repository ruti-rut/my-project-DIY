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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 1. קבלת פרטי המשתמש מגוגל
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 2. חיפוש המשתמש ב-DB
        Optional<Users> userOptional = usersRepository.findByMail(email);

        // 3. אם המשתמש לא קיים - דחה כניסה
        if (!userOptional.isPresent()) {
            System.out.println("❌ User with email " + email + " not found in database.");
            String redirectUrl = "http://localhost:4200/sign-in?error=user_not_found";
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
            return;
        }

        // 4. אם המשתמש קיים - יצור JWT
        Users user = userOptional.get();
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String jwt = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        // 5. הפנה ל-Angular עם ה-Token ב-URL
        String targetUrl = "http://localhost:4200/oauth2/callback?token=" + jwt;
        System.out.println("✅ Redirecting to: " + targetUrl);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

