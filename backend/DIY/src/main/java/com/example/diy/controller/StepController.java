package com.example.diy.controller;

import com.example.diy.DTO.StepDTO;
import com.example.diy.DTO.StepResponseDTO;
import com.example.diy.Mapper.StepMapper;
import com.example.diy.model.Project;
import com.example.diy.model.Step;
import com.example.diy.model.Users;
import com.example.diy.service.ImageUtils;
import com.example.diy.service.ProjectRepository;
import com.example.diy.service.StepRepository;
import com.example.diy.service.UsersRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/api/step")

public class StepController {
    private static final Logger logger = LoggerFactory.getLogger(StepController.class); // ✅ הוספת Logger
    StepRepository stepRepository;
    StepMapper stepMapper;
    ProjectRepository projectRepository;
    UsersRepository usersRepository;

    public StepController(StepRepository stepRepository, StepMapper stepMapper, ProjectRepository projectRepository, UsersRepository usersRepository) {
        this.stepRepository = stepRepository;
        this.stepMapper = stepMapper;
        this.projectRepository = projectRepository;
        this.usersRepository = usersRepository;
    }

    // -----------------------------------------------------------
    // ------------------- Helper Methods -----------------------
    // -----------------------------------------------------------


    /**
     * Retrieves the Users entity for the currently authenticated user.
     *
     * @param principal The security principal.
     * @return The Users entity.
     * @throws ResponseStatusException with status 401 if the user is not found.
     */
    private Users getCurrentUser(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated.");
        }
        String username = principal.getName();
        return usersRepository.findByUserName(username);
    }

    /**
     * Checks if the authenticated user is the owner of the given project.
     *
     * @param project     The project entity to check ownership against.
     * @param currentUser The Users entity of the authenticated user.
     * @throws ResponseStatusException with status 403 if the user is not the owner.
     */
    private void checkProjectOwnership(Project project, Users currentUser) {
        if (!project.getUsers().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the owner of the project and cannot modify its steps.");
        }
    }

    // -----------------------------------------------------------
    // ------------------- POST Endpoints -----------------------
    // -----------------------------------------------------------


    /**
     * Creates a new step and optionally uploads an associated image.
     * Performs DTO validation and checks for project existence and user authorization.
     *
     * @param file      The step image file (optional).
     * @param s         The DTO containing the step details (validated using @Valid).
     * @param principal The security principal of the currently logged-in user (for authorization).
     * @return A ResponseEntity containing the StepResponseDTO of the created step with HttpStatus.CREATED.
     * @throws ResponseStatusException with status 404 if the Project ID is not found.
     * @throws ResponseStatusException with status 403 if the user is not the project owner.
     */
    @PostMapping("/uploadStep")
    public ResponseEntity<StepResponseDTO> uploadStepWithImage(
            @RequestPart(value = "image", required = false) MultipartFile file,
            @RequestPart("step") @Valid StepDTO s,
            Principal principal) {
        try {
            Users currentUser = getCurrentUser(principal);
            if (currentUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

            Project project = projectRepository.findById(s.getProjectId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

            checkProjectOwnership(project, currentUser);

            Step step = stepMapper.stepDtoToStep(s);
            step.setProject(project);
            // ✅ אם יש תמונה חדשה - שמור אותה
            if (file != null && !file.isEmpty()) {
                ImageUtils.uploadImage(file);
                step.setPicturePath(file.getOriginalFilename());
            }
            // ✅ אם אין תמונה חדשה - בדוק אם יש picturePath בDTO
            else if (s.getPicturePath() != null && !s.getPicturePath().trim().isEmpty()) {
                step.setPicturePath(s.getPicturePath());
            }
            // אחרת - picturePath יהיה null

            Step savedStep = stepRepository.save(step);
            StepResponseDTO responseDTO = stepMapper.stepEntityToResponseDTO(savedStep);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (IOException e) {
            logger.error("Error during step image upload: {}", e.getMessage(), e); // ✅ שימוש ב-logger
            return ResponseEntity.internalServerError().build();
        } catch (ResponseStatusException e) {
            throw e; // מאפשר ל-Spring לטפל בשגיאות סטטוס (404, 403, 401)
        } catch (Exception e) {
            logger.error("An unexpected error occurred during step creation: {}", e.getMessage(), e); // ✅ שימוש ב-logger
            return ResponseEntity.internalServerError().build();
        }
    }

    // -----------------------------------------------------------
    // ------------------- PUT / PATCH Endpoints ----------------
    // -----------------------------------------------------------


    /**
     * Updates an existing step and optionally replaces its image.
     * Performs DTO validation and checks for step existence and project ownership.
     *
     * @param stepId         The ID of the step to update.
     * @param file           The new step image file (optional).
     * @param updatedStepDto The DTO containing the updated step details (validated using @Valid).
     * @param principal      The security principal of the currently logged-in user (for authorization).
     * @return A ResponseEntity containing the updated StepResponseDTO.
     * @throws ResponseStatusException with status 404 if the step is not found.
     * @throws ResponseStatusException with status 403 if the user is not the project owner.
     */
    @PutMapping("/editStepWithImage/{stepId}")
    public ResponseEntity<StepResponseDTO> editStepWithImage(
            @PathVariable Long stepId,
            @RequestPart(value = "image", required = false) MultipartFile file, // התמונה היא אופציונלית
            @RequestPart("step") @Valid StepDTO updatedStepDto,
            Principal principal) {
        try {
            Users currentUser = getCurrentUser(principal);
            if (currentUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

            Step existingStep = stepRepository.findById(stepId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Step not found."));

            Project project = existingStep.getProject();
            checkProjectOwnership(project, currentUser);
            stepMapper.updateStepFromDto(updatedStepDto, existingStep);

            if (file != null && !file.isEmpty()) {
                ImageUtils.uploadImage(file);
                existingStep.setPicturePath(file.getOriginalFilename());
            }

            // 4. שמירת השלב המעודכן
            Step savedStep = stepRepository.save(existingStep);
            StepResponseDTO responseDTO = stepMapper.stepEntityToResponseDTO(savedStep);
            return ResponseEntity.ok(responseDTO);

        } catch (IOException e) {
            logger.error("Error during step image upload on update (ID: {}): {}", stepId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during step update (ID: {}): {}", stepId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // -----------------------------------------------------------
    // ------------------- DELETE Endpoints ---------------------
    // -----------------------------------------------------------


    /**
     * Deletes all steps belonging to a specific project.
     * Ensures only the project owner can perform this operation.
     *
     * @param projectId The ID of the project whose steps should be deleted.
     * @param principal The security principal of the currently logged-in user (for authorization).
     * @return A ResponseEntity with HttpStatus.NO_CONTENT (204) upon successful deletion.
     * @throws ResponseStatusException with status 404 if the project is not found.
     * @throws ResponseStatusException with status 403 if the user is not the project owner.
     */
    @DeleteMapping("/deleteAllByProject/{projectId}")
    public ResponseEntity<Void> deleteAllStepsByProject(@PathVariable Long projectId, Principal principal) {
        try {
            Users currentUser = getCurrentUser(principal);
            if (currentUser == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found.");

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found."));

            checkProjectOwnership(project, currentUser);
            stepRepository.deleteByProjectId(projectId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting steps for project (ID: {}): {}", projectId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/deleteStep/{stepId}")
    public ResponseEntity<Void> deleteStep(@PathVariable Long stepId) {
        try {
            if (!stepRepository.existsById(stepId)) {
                return ResponseEntity.notFound().build();
            }
            stepRepository.deleteById(stepId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            System.out.println("Error deleting step: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }


}