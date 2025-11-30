package com.example.diy.controller;

import com.example.diy.DTO.UserLogInDTO;
import com.example.diy.DTO.UserResponseDTO;
import com.example.diy.DTO.UsersRegisterDTO;
import com.example.diy.Mapper.UsersMapper;
import com.example.diy.model.AuthProvider;
import com.example.diy.model.Users;
import com.example.diy.security.CustomUserDetails;
import com.example.diy.security.jwt.JwtUtils;
import com.example.diy.service.EmailSenderService;
import com.example.diy.service.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    private final PasswordEncoder passwordEncoder; // הוספנו את ה-PasswordEncoder
    UsersRepository usersRepository;
    UsersMapper usersMapper;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    EmailSenderService emailSenderService;

    public AuthController(PasswordEncoder passwordEncoder, UsersRepository usersRepository, UsersMapper usersMapper, AuthenticationManager authenticationManager, JwtUtils jwtUtils, EmailSenderService emailSenderService) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.emailSenderService = emailSenderService;
    }
    // --- 2. כניסת משתמש (Signin) ---
    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody UserLogInDTO loginRequest) {

        try {
            // 1. אימות המשתמש (מפעיל את DaoAuthenticationProvider)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 3. קבלת פרטי המשתמש
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            // 4. יצירת JWT והצבת ה-Cookie
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
            UserResponseDTO responseDto = usersMapper.usersToUserResponseDTO(userDetails.getUser());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(responseDto);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(Map.of("error", "שם משתמש או סיסמה שגויים."), HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        Users user = usersRepository.findByUserName(principal.getName());
        return ResponseEntity.ok(usersMapper.usersToUserResponseDTO(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cleanCookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                .body(Map.of("message", "התנתקת בהצלחה!"));
    }
    @GetMapping("/login")
    public void login(@RequestParam(value = "error", required = false) String error,
                      HttpServletResponse response) throws IOException {

        if (error != null) {
            String redirectUrl = "http://localhost:4200/login?error=true";
            response.sendRedirect(redirectUrl);
            return;
        }

        response.sendRedirect("/oauth2/authorization/google");
    }
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {

        Users user = usersRepository.findByVerificationToken(token);

        if (user == null) {
            return ResponseEntity.badRequest().body("Token לא תקף");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        usersRepository.save(user);

        return ResponseEntity.ok("המייל אומת בהצלחה!");
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signUp(@Valid @RequestBody UsersRegisterDTO user) {
        try {
            if (usersRepository.existsByUserName(user.getUserName())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Users newUser = usersMapper.usersRegisterDTOToUsers(user);

            String encodedPassword = passwordEncoder.encode(user.getPassword());
            newUser.setPassword(encodedPassword);

            newUser.setProvider(AuthProvider.LOCAL);

            // יצירת טוקן אימות
            String token = UUID.randomUUID().toString();
            newUser.setVerificationToken(token);
            newUser.setEmailVerified(false);

            Users saved = usersRepository.save(newUser);

            // שליחת מייל אימות - בתוך try-catch נפרד
            try {
                String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;

                emailSenderService.send(
                        newUser.getMail(),
                        "אימות מייל",
                        "<h2>ברוכה הבאה!</h2>" +
                                "<p>לחצי על הלינק כדי לאמת את המייל:</p>" +
                                "<a href=\"" + verifyLink + "\">אימות מייל</a>"
                );
            } catch (Exception emailException) {
                // ✅ שגיאה בשליחת מייל לא תעצור את הrequest
                System.err.println("Failed to send verification email to: " + newUser.getMail());
                emailException.printStackTrace();
                // המשך עם הrequest בכל מקרה
            }

            UserResponseDTO responseDto = usersMapper.usersToUserResponseDTO(saved);

            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Signup failed: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
