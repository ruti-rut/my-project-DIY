package com.example.diy.controller;

import com.example.diy.model.Challenge;
import com.example.diy.service.ChallengeRepository;
import com.example.diy.service.ImageUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@RequestMapping("/api/challenge")
@CrossOrigin

public class ChallengeController {
    ChallengeRepository challengeRepository;

    public ChallengeController(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @GetMapping("/challenge/{id}")
    public ResponseEntity<Challenge> getChallenge(@PathVariable Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        return ResponseEntity.ok(challenge);
    }


    @PostMapping("/uploadChallenge")
    public ResponseEntity<Challenge> uploadChallengeWithImage(@RequestPart("image") MultipartFile file
            ,@RequestPart("challenge") Challenge c) {
        try {
            ImageUtils.uploadImage(file);
            c.setPicturePath(file.getOriginalFilename());
            Challenge challenge=challengeRepository.save(c);
            return new ResponseEntity<>(challenge, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
