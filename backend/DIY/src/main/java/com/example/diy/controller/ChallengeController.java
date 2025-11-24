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
        List<Challenge> list = challengeRepository.findAll();
        if (list != null) {
            return new ResponseEntity<>(challengeMapper.toChallengeListDTOList(list), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/challenge/{id}")
    public ResponseEntity<ChallengeResponseDTO> getChallenge(@PathVariable Long id) {
        Challenge challenge = challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        ChallengeResponseDTO dto = challengeMapper.toChallengeResponseDTO(challenge);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/uploadChallenge")
    public ResponseEntity<Challenge> uploadChallengeWithImage(@RequestPart("image") MultipartFile file
            , @RequestPart("challenge") ChallengeCreateDTO challengeDto) {
        try {
            ImageUtils.uploadImage(file);
            Challenge challenge = challengeMapper.challengeCreateDTOToEntity(challengeDto);
            challenge.setPicturePath(file.getOriginalFilename());

            Challenge savedChallenge = challengeRepository.save(challenge);
            return new ResponseEntity<>(savedChallenge, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
