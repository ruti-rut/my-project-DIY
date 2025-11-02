package com.example.diy.controller;

import com.example.diy.DTO.StepDTO;
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
@CrossOrigin

public class StepController {
    StepRepository stepRepository;
    StepMapper stepMapper;
    ProjectRepository projectRepository;

    @Autowired
    public StepController(StepRepository stepRepository, StepMapper stepMapper) {
        this.stepRepository = stepRepository;
        this.stepMapper = stepMapper;
    }

    @PostMapping("/uploadStep")
    public ResponseEntity<Step> uploadStepWithImage(@RequestPart("image") MultipartFile file
            , @RequestPart("step") StepDTO s) {
        try {
            ImageUtils.uploadImage(file);

            Step step = stepMapper.stepDTOToStep(s);

            Project project = projectRepository.findById(s.getIdProject())
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            step.setProject(project);
            step.setPicturePath(file.getOriginalFilename());

            Step savedStep = stepRepository.save(step);

            return new ResponseEntity<>(savedStep, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
