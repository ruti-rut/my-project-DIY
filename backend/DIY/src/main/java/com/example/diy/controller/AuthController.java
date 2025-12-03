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
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for handling user authentication and authorization operations,
 * including registration, login, logout, email verification, and fetching user details.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class); // ✅ הוספת Logger
    private final PasswordEncoder passwordEncoder; // הוספנו את ה-PasswordEncoder
    UsersRepository usersRepository;
    UsersMapper usersMapper;
    EmailSenderService emailSenderService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    public AuthController(PasswordEncoder passwordEncoder, UsersRepository usersRepository, UsersMapper usersMapper, AuthenticationManager authenticationManager, JwtUtils jwtUtils, EmailSenderService emailSenderService) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.emailSenderService = emailSenderService;
    }
    // ----------------------------------------------------------------------------------
    // ---------------------------- REGISTRATION ----------------------------------------
    // ----------------------------------------------------------------------------------

    /**
     * Registers a new user. Performs DTO validation, checks for unique username and email,
     * encodes the password, saves the user, and sends a verification email.
     *
     * @param user The DTO containing the user's registration details (validated using @Valid).
     * @return A ResponseEntity containing the UserResponseDTO of the created user with HttpStatus.CREATED.
     * @throws ResponseStatusException with status 400 if the username or email already exists.
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signUp(@Valid @RequestBody UsersRegisterDTO user) {
        try {
            // 1. וולידציית ייחודיות
            if (usersRepository.existsByUserName(user.getUserName())) {
                // ✅ שימוש ב-ResponseStatusException לטיפול אחיד ב-400
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "שם משתמש זה כבר קיים.");
            }
            if (usersRepository.existsByMail(user.getMail())) { // ✅ בדיקת ייחודיות למייל
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "כתובת דואר אלקטרוני זו כבר רשומה.");
            }

            Users newUser = usersMapper.usersRegisterDTOToUsers(user);

            // 2. הצפנת סיסמה והגדרת ספק
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            newUser.setPassword(encodedPassword);
            newUser.setProvider(AuthProvider.LOCAL);

            // 3. יצירת טוקן אימות
            String token = UUID.randomUUID().toString();
            newUser.setVerificationToken(token);
            newUser.setEmailVerified(false);

            Users saved = usersRepository.save(newUser);

            // 4. שליחת מייל אימות (בלוגיקה נפרדת)
            sendVerificationEmail(newUser.getMail(), token);

            UserResponseDTO responseDto = usersMapper.usersToUserResponseDTO(saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);

        } catch (ResponseStatusException e) {
            throw e; // מאפשר ל-Spring לטפל בשגיאות 400
        } catch (Exception e) {
            logger.error("Signup failed for user {}: {}", user.getUserName(), e.getMessage(), e); // ✅ שימוש ב-logger
            return ResponseEntity.internalServerError().build();
        }
    }

    // ----------------------------------------------------------------------------------
    // ---------------------------- LOGIN / LOGOUT --------------------------------------
    // ----------------------------------------------------------------------------------


    /**
     * Handles user login by authenticating the credentials (username/email and password).
     * Upon success, creates a JWT and sets it as an HTTP-only cookie.
     *
     * @param loginRequest The DTO containing the identifier and password (validated using @Valid).
     * @return A ResponseEntity containing the UserResponseDTO and the JWT cookie header.
     */
    @PostMapping("/signin")
    public ResponseEntity<?> login(@Valid @RequestBody UserLogInDTO loginRequest) {
        try {
            // 1. אימות המשתמש (מפעיל את DaoAuthenticationProvider)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getIdentifier(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            // 4. יצירת JWT והצבת ה-Cookie
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
            UserResponseDTO responseDto = usersMapper.usersToUserResponseDTO(userDetails.getUser());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(responseDto);

        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for identifier: {}", loginRequest.getIdentifier());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "שם משתמש או סיסמה שגויים."));
        } catch (Exception e) {
            logger.error("Internal server error during signin:", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "שגיאת שרת פנימית במהלך ההתחברות."));
        }
    }

    /**
     * Handles user logout by instructing the client to remove the JWT cookie.
     *
     * @return A ResponseEntity with a clean JWT cookie header and a success message.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        try {
            ResponseCookie cleanCookie = jwtUtils.getCleanJwtCookie();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cleanCookie.toString())
                    .body(Map.of("message", "התנתקת בהצלחה!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    // ----------------------------------------------------------------------------------
    // ---------------------------- UTILITY & VERIFICATION ------------------------------
    // ----------------------------------------------------------------------------------

    /**
     * Endpoint for current user details. Requires a valid JWT token.
     *
     * @param principal The authenticated user's principal.
     * @return A ResponseEntity containing the UserResponseDTO.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Users user = usersRepository.findByUserName(principal.getName());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(usersMapper.usersToUserResponseDTO(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    /**
     * Handles the email verification link clicked by the user.
     *
     * @param token The unique verification token sent in the email.
     * @return A ResponseEntity with a success/failure message.
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            Users user = usersRepository.findByVerificationToken(token);

            if (user == null) {
                return ResponseEntity.badRequest().body("Token לא תקף");
            }

            user.setEmailVerified(true);
            user.setVerificationToken(null);
            usersRepository.save(user);

            return ResponseEntity.ok("המייל אומת בהצלחה!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("שגיאה במהלך אימות המייל.");
        }
    }


    /**
     * Utility method to handle sending the verification email.
     * Separated for cleaner main signup logic.
     *
     * @param email The recipient email address.
     * @param token The verification token to include in the link.
     */
    private void sendVerificationEmail(String email, String token) {
        try {
            // ✅ הערה: יש לשנות את localhost:8080 לכתובת הציבורית של השרת שלך
            String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;

            emailSenderService.send(
                    email,
                    "אימות מייל ל-DIY Project",
                    "<h2>ברוכה הבאה!</h2>" +
                            "<p>לחצי על הלינק כדי לאמת את המייל:</p>" +
                            "<a href=\"" + verifyLink + "\">אימות מייל</a>"
            );
        } catch (Exception emailException) {
            // ✅ רישום שגיאה ב-logger במקום System.err
            logger.error("Failed to send verification email to: {}", email, emailException);
            // ממשיך, לא זורק שגיאה כי יצירת המשתמש הצליחה
        }
    }

    //    @GetMapping("/login")
//    public void login(@RequestParam(value = "error", required = false) String error,
//                      HttpServletResponse response) throws IOException {
//
//        if (error != null) {
//            String redirectUrl = "http://localhost:4200/login?error=true";
//            response.sendRedirect(redirectUrl);
//            return;
//        }
//
//        response.sendRedirect("/oauth2/authorization/google");
//    }

}