package com.example.diy.controller;

import com.example.diy.DTO.HomeResponseDTO;
import com.example.diy.model.Users;
import com.example.diy.service.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    // הנחה: אתה יכול לקבל את המשתמש ישירות מה-Security Context
    @GetMapping("/all")
    public ResponseEntity<HomeResponseDTO> getHomeData(@AuthenticationPrincipal Users currentUser) {
        HomeResponseDTO response = homeService.getHomeData(currentUser);
        return ResponseEntity.ok(response);
    }
}