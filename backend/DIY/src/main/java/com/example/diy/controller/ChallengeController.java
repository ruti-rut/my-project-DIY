package com.example.diy.controller;

import com.example.diy.DTO.ChallengeCreateDTO;
import com.example.diy.DTO.ChallengeListDTO;
import com.example.diy.DTO.ChallengeResponseDTO;
import com.example.diy.Mapper.ChallengeMapper;
import com.example.diy.model.Challenge;
import com.example.diy.model.Project;
import com.example.diy.service.ChallengeRepository;
import com.example.diy.service.ImageUtils;
import com.example.diy.service.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/challenge")

public class ChallengeController {
    ChallengeRepository challengeRepository;
    ChallengeMapper challengeMapper;
    ProjectRepository projectRepository;

    public ChallengeController(ChallengeRepository challengeRepository, ChallengeMapper challengeMapper, ProjectRepository projectRepository) {
        this.challengeRepository = challengeRepository;
        this.challengeMapper = challengeMapper;
        this.projectRepository = projectRepository;
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

    @GetMapping("/challenge/{id}")
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

    @PostMapping("/uploadChallenge")
    public ResponseEntity<Challenge> uploadChallengeWithImage(@RequestPart("image") MultipartFile file
            , @RequestPart("challenge") ChallengeCreateDTO challengeDto) {
        try {
            ImageUtils.uploadImage(file);
            Challenge challenge = challengeMapper.challengeCreateDTOToEntity(challengeDto);
            challenge.setPicturePath(file.getOriginalFilename());

            Challenge savedChallenge = challengeRepository.save(challenge);

            // שימוש בשיטת נוחות: Created (201)
            // נמנעים מלהעביר null ל-created ומשתמשים ב-status() או בבניית URI כפי שהוסבר
            // לצורך היצמדות מינימלית, נשתמש ב-status(CREATED) כדי להימנע מה-@NotNull warning
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