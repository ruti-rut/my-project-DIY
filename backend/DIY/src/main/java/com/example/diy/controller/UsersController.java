package com.example.diy.controller;

import com.example.diy.DTO.ChatRequest;
import com.example.diy.DTO.UserProfileDTO;
import com.example.diy.DTO.UserResponseDTO;
import com.example.diy.Mapper.UsersMapper;
import com.example.diy.model.Users;
import com.example.diy.service.AIChatService;
import com.example.diy.service.ImageUtils;
import com.example.diy.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")

public class UsersController {
    UsersRepository usersRepository;
    UsersMapper usersMapper;
    private AIChatService aiChatService;

    @Autowired
    public UsersController(UsersRepository usersRepository, UsersMapper usersMapper, AIChatService aiChatService) {
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
        this.aiChatService = aiChatService;
    }

    // *** ENDPOINT לניהול הרשמה: PUT /api/users/subscription/toggle ***
    @PutMapping("/subscription/toggle")
    public ResponseEntity<UserResponseDTO> toggleSubscription(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Users user = usersRepository.findByUserName(principal.getName());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            boolean newStatus = !user.isSubscribedToDaily();
            user.setSubscribedToDaily(newStatus);
            Users updatedUser = usersRepository.save(user);

            // ← חשוב! תחזיר את ה-user המעודכן עם כל השדות!
            UserResponseDTO responseDto = usersMapper.usersToUserResponseDTO(updatedUser);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getMyProfile(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Users user = usersRepository.findByUserName(principal.getName());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // שדות אלה צריכים להיות מחושבים אוטומטית במאפר או ב-DTO


            UserProfileDTO profileDTO = usersMapper.usersToUserProfileDTO(user);

            return ResponseEntity.ok(profileDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/me/update-profile")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @RequestPart(required = false) MultipartFile file,
            @RequestPart(required = false) String city,
            @RequestPart(required = false) String aboutMe,
            Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Users user = usersRepository.findByUserName(principal.getName());
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            // עדכון שדות טקסט
            if (city != null && !city.trim().isEmpty()) {
                user.setCity(city.trim());
            }
            if (aboutMe != null && !aboutMe.trim().isEmpty()) {
                user.setAboutMe(aboutMe.trim());
            }

            // עדכון תמונה
            if (file != null && !file.isEmpty()) {
                ImageUtils.uploadImage(file);
                // שמירת הנתיב ללא /images/ - נוסיף אותו רק בהחזרה
                user.setProfilePicturePath(file.getOriginalFilename());
            }

            Users savedUser = usersRepository.save(user);
            UserProfileDTO profileDTO = usersMapper.usersToUserProfileDTO(savedUser);

            return ResponseEntity.ok(profileDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}