package com.example.diy.controller;

import com.example.diy.DTO.ChallengeCreateDTO;
import com.example.diy.DTO.ChallengeListDTO;
import com.example.diy.DTO.ChallengeResponseDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.Mapper.ChallengeMapper;
import com.example.diy.Mapper.ProjectMapper;
import com.example.diy.model.Challenge;
import com.example.diy.model.Project;
import com.example.diy.model.Users;
import com.example.diy.security.CustomUserDetails;
import com.example.diy.service.ChallengeRepository;
import com.example.diy.service.ImageUtils;
import com.example.diy.service.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/challenge")

public class ChallengeController {
    ChallengeRepository challengeRepository;
    ChallengeMapper challengeMapper;
    ProjectRepository projectRepository;
    ProjectMapper projectMapper;

    public ChallengeController(ChallengeRepository challengeRepository, ChallengeMapper challengeMapper, ProjectRepository projectRepository, ProjectMapper projectMapper) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
    }

    @GetMapping("/allChallenges")
    public ResponseEntity<List<ChallengeListDTO>> getAllChallenges() {
        try {
            List<Challenge> list = challengeRepository.findAll();

            if (list.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(challengeMapper.toChallengeListDTOList(list));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChallengeResponseDTO> getChallenge(@PathVariable Long id) {
        try {
            Challenge challenge = challengeRepository.findByIdWithProjectsAndUsers(id)
                    .orElseThrow(() -> new RuntimeException("Challenge not found"));

            ChallengeResponseDTO dto = challengeMapper.toChallengeResponseDTO(challenge);
            return ResponseEntity.ok(dto);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("Challenge not found")) {
                return ResponseEntity.notFound().build();
            }
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{challengeId}/projects")
    public ResponseEntity<List<ProjectListDTO>> getProjectsByChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        final Users currentUser = userDetails != null ? userDetails.getUser() : null;
        try {
            List<Project> projects = projectRepository.findProjectsByChallengeIdWithUsers(challengeId);

            List<ProjectListDTO> dtos = projects.stream()
                    .map(p -> projectMapper.toProjectListDTO(p, currentUser))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/uploadChallenge")
    public ResponseEntity<Challenge> uploadChallengeWithImage(@RequestPart("image") MultipartFile file
            , @RequestPart("challenge") ChallengeCreateDTO challengeDto) {
        try {
            ImageUtils.uploadImage(file);
            Challenge challenge = challengeMapper.challengeCreateDTOToEntity(challengeDto);
            challenge.setPicturePath(file.getOriginalFilename());

            Challenge savedChallenge = challengeRepository.save(challenge);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedChallenge);

        } catch (IOException e) {
            System.err.println("Error uploading image: " + e.getMessage());
            // שימוש בשיטת נוחות: Internal Server Error (500)
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            System.err.println("Error saving challenge: " + e.getMessage());
            e.printStackTrace();
            // שימוש בשיטת נוחות: Internal Server Error (500)
            return ResponseEntity.internalServerError().build();
        }
    }
}