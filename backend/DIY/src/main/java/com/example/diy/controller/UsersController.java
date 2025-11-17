package com.example.diy.controller;

import com.example.diy.DTO.UserResponseDTO;
import com.example.diy.Mapper.UsersMapper;
import com.example.diy.model.Users;
import com.example.diy.service.UsersRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")

public class UsersController {
    UsersRepository usersRepository;
    UsersMapper usersMapper;


    public UsersController(UsersRepository usersRepository, UsersMapper usersMapper) {
        this.usersRepository = usersRepository;
        this.usersMapper = usersMapper;
    }
    // *** ENDPOINT לניהול הרשמה: PUT /api/users/subscription/toggle ***
    @PutMapping("/subscription/toggle")
    public ResponseEntity<UserResponseDTO> toggleSubscription(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Users user = usersRepository.findByUserName(principal.getName());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        boolean newStatus = !user.isSubscribedToDaily();
        user.setSubscribedToDaily(newStatus);
        Users updatedUser = usersRepository.save(user);

        // ← חשוב! תחזיר את ה-user המעודכן עם כל השדות!
        UserResponseDTO responseDto = usersMapper.usersToUserResponseDTO(updatedUser);

        return ResponseEntity.ok(responseDto);  // ← חייב להחזיר את זה!
    }
}


