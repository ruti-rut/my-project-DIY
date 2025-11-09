package com.example.diy.controller;

import com.example.diy.DTO.UserLogInDTO;
import com.example.diy.DTO.UserResponseDTO;
import com.example.diy.DTO.UsersRegisterDTO;
import com.example.diy.Mapper.UsersMapper;
import com.example.diy.model.AuthProvider;
import com.example.diy.model.Users;
import com.example.diy.security.CustomUserDetails;
import com.example.diy.security.jwt.JwtUtils;
import com.example.diy.service.UsersRepository;
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

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    private final PasswordEncoder passwordEncoder; // הוספנו את ה-PasswordEncoder
    UsersRepository usersRepository;
    UsersMapper usersMapper;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;

    public AuthController(PasswordEncoder passwordEncoder, UsersRepository usersRepository, UsersMapper usersMapper, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDTO> signUp(@Valid @RequestBody UsersRegisterDTO user) {
        // נבדוק ששם המשתמש (או המזהה) לא קיים
        if (usersRepository.existsByUserName(user.getUserName())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // 400 Bad Request
        }
        Users newUser = usersMapper.usersRegisterDTOToUsers(user);
        // הצפנת סיסמה באמצעות ה-PasswordEncoder המוזרק
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        newUser.setPassword(encodedPassword);
        // הגדרת ספק האימות כ-LOCAL
        newUser.setProvider(AuthProvider.LOCAL);
        Users savedUser = usersRepository.save(newUser);
        UserResponseDTO responseDto = usersMapper.usersToUserResponseDTO(savedUser);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // 201 Created
    }

    // --- 2. כניסת משתמש (Login) ---
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

}
