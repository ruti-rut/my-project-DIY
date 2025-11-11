package com.example.diy.controller;

import com.example.diy.DTO.StepDTO;
import com.example.diy.DTO.StepResponseDTO;
import com.example.diy.Mapper.StepMapper;
import com.example.diy.model.Project;
import com.example.diy.model.Step;
import com.example.diy.service.ImageUtils;
import com.example.diy.service.ProjectRepository;
import com.example.diy.service.StepRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/step")

public class StepController {
    StepRepository stepRepository;
    StepMapper stepMapper;
    ProjectRepository projectRepository;

    public StepController(StepRepository stepRepository, StepMapper stepMapper, ProjectRepository projectRepository) {
        this.stepRepository = stepRepository;
        this.stepMapper = stepMapper;
        this.projectRepository = projectRepository;
    }

    @PostMapping("/uploadStep")
    public ResponseEntity<StepResponseDTO> uploadStepWithImage(@RequestPart("image") MultipartFile file
            , @RequestPart("step") StepDTO s) {
        try {
            ImageUtils.uploadImage(file);

            Step step = stepMapper.stepDtoToStep(s);

            Project project = projectRepository.findById(s.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            step.setProject(project);
            step.setPicturePath(file.getOriginalFilename());

            Step savedStep = stepRepository.save(step);
            StepResponseDTO responseDTO = stepMapper.stepEntityToResponseDTO(savedStep);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/deleteStep/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long stepId) {
        if (!stepRepository.existsById(stepId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try {
            stepRepository.deleteById(stepId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            System.out.println("Error deleting step: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/editStepWithImage/{stepId}")
    public ResponseEntity<StepResponseDTO> editStepWithImage(
            @PathVariable Long stepId,
            @RequestPart(value = "image", required = false) MultipartFile file, // התמונה היא אופציונלית
            @RequestPart("step") StepDTO updatedStepDto) {
        // 1. חיפוש השלב הקיים
        Step existingStep = stepRepository.findById(stepId).orElse(null);

        if (existingStep == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        try {
            // 2. עדכון שדות השלב באמצעות המאפר (השיפור)
            stepMapper.updateStepFromDto(updatedStepDto, existingStep);
            // 3. טיפול בהחלפת התמונה (אם הועלתה תמונה חדשה)
            if (file != null && !file.isEmpty()) {
                // קוד להעלאת התמונה החדשה
                ImageUtils.uploadImage(file);

                // עדכון נתיב התמונה
                existingStep.setPicturePath(file.getOriginalFilename());
            }
            // 4. שמירת השלב המעודכן
            Step savedStep = stepRepository.save(existingStep);
            StepResponseDTO responseDTO = stepMapper.stepEntityToResponseDTO(savedStep);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (IOException e) {
            System.out.println("Error uploading image: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
