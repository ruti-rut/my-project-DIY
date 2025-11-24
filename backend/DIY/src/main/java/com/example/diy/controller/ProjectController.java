package com.example.diy.controller;


import com.example.diy.DTO.ProjectCreateDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.DTO.ProjectResponseDTO;
import com.example.diy.Mapper.ProjectMapper;
import com.example.diy.model.Challenge;
import com.example.diy.model.Project;
import com.example.diy.model.Tag;
import com.example.diy.model.Users;
import com.example.diy.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    ProjectRepository projectRepository;
    ProjectMapper projectMapper;
    UsersRepository usersRepository;
    TagRepository tagRepository;
    HomeService homeService;
    ChallengeRepository  challengeRepository;


    public ProjectController(ProjectRepository projectRepository, ProjectMapper projectMapper, UsersRepository usersRepository, TagRepository tagRepository,HomeService homeService,ChallengeRepository  challengeRepository) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.usersRepository = usersRepository;
        this.tagRepository = tagRepository;
        this.homeService = homeService;
        this.challengeRepository = challengeRepository;
    }

    @GetMapping("/getProject/{id}")
    public ResponseEntity<ProjectResponseDTO> get(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(project -> ResponseEntity.ok(projectMapper.projectEntityToResponseDTO(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/uploadProject")
    public ResponseEntity<ProjectResponseDTO> uploadProjectWithImage(@RequestPart("image") MultipartFile file,
                                                                     @RequestPart("project") ProjectCreateDTO p,
                                                                     Principal principal) {
        try {
            ImageUtils.uploadImage(file);
            Project project = projectMapper.projectCreateDTOToEntity(p);

            // 1. מציאת משתמש וקישור
            Users currentUser = getCurrentUser(principal);
            project.setUsers(currentUser);
            project.setPicturePath(file.getOriginalFilename());

            // 🌟 2. טיפול בתגיות באמצעות ה-TagRepository 🌟
            Set<Tag> tags = new HashSet<>();
            List<String> tagNames = p.getTagNames(); // רשימת שמות התגיות מה-DTO

            if (tagNames != null && !tagNames.isEmpty()) {
                // א. מציאת תגיות קיימות
                List<Tag> existingTags = tagRepository.findByNameIn(tagNames);
                Set<String> existingNames = existingTags.stream().map(Tag::getName).collect(Collectors.toSet());
                tags.addAll(existingTags);

                // ב. יצירת ושמירת תגיות חדשות
                tagNames.stream().filter(name -> !existingNames.contains(name)).forEach(name -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    tags.add(tagRepository.save(newTag)); // שמירה ישירה
                });
            }
            project.setTags(tags); // קישור ה-Set<Tag> לפרויקט

            Project savedProject = projectRepository.save(project);
            // 3. מיפוי לתגובה
            ProjectResponseDTO responseDTO = projectMapper.projectEntityToResponseDTO(savedProject);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProjectListDTO>> getProjectsByCategory(@PathVariable Long categoryId) {
        List<Project> projects = projectRepository.findByCategoryId(categoryId);
        if (projects != null)
            return new ResponseEntity<>(projectMapper.toProjectListDTOList(projects), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/editProject/{id}")
    public ResponseEntity<Project> updateProjectWithImage(@PathVariable Long id,
                                                          @RequestPart(value = "image", required = false) MultipartFile file,
                                                          @RequestPart("project") ProjectCreateDTO p) {
        try {
            Project existingProject = projectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
            // 2. מיפוי נתונים חדשים מה-DTO לאובייקט הקיים
            // שימוש במאפר שיודע לעדכן (למשל, mapstruct)
            Project updatedProject = projectMapper.updateProjectFromDto(p, existingProject);
            // 3. טיפול בתמונה (רק אם נשלחה תמונה חדשה)
            if (file != null && !file.isEmpty()) {
                // אם יש קובץ חדש: שמירה שלו ועדכון הנתיב
                ImageUtils.uploadImage(file);
                updatedProject.setPicturePath(file.getOriginalFilename());
            }
            // אם לא נשלח קובץ חדש, הנתיב הקיים נשמר.
            // 4. שמירת הפרויקט המעודכן (יעדכן את הרשומה הקיימת בגלל שה-ID קיים)
            Project savedProject = projectRepository.save(updatedProject);
            return new ResponseEntity<>(savedProject, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{projectId}/favorite")
    public ResponseEntity<Void> addToFavorites(@PathVariable Long projectId,
                                               Principal principal) {
        Users currentUser = getCurrentUser(principal);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!currentUser.getFavoriteProjects().contains(project)) {
            currentUser.getFavoriteProjects().add(project);
            usersRepository.save(currentUser); // שומר את הקשר
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/favorite")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long projectId, Principal principal) {
        Users currentUser = getCurrentUser(principal);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        currentUser.getFavoriteProjects().remove(project);
        usersRepository.save(currentUser);

        return ResponseEntity.ok().build();
    }

    // פונקציית עזר
    private Users getCurrentUser(Principal principal) {
        String username = principal.getName(); // מהטוקן
        return usersRepository.findByUserName(username);
    }


    @GetMapping("/allProjects")
    public ResponseEntity<Page<ProjectListDTO>> getAllProjectsWithFilters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> categoryIds,  // 🔥 מערך!
            @RequestParam(defaultValue = "newest") String sort
    ) {
        try {
            Pageable pageable;

            // טיפול במיון
            switch (sort) {
                case "oldest":
                    pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
                    break;
                case "popular":
                    pageable = PageRequest.of(page, size);
                    break;
                default: // newest
                    pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            }

            Page<Project> projects;

            // לוגיקת החיפוש
            if (search != null && !search.trim().isEmpty()) {
                // יש חיפוש
                if (categoryIds != null) {
                    // חיפוש + קטגוריה
                    projects = projectRepository.searchByTitleOrTagsAndCategories(search, categoryIds, pageable);
                } else {
                    // חיפוש בלי קטגוריה
                    if ("popular".equals(sort)) {
                        projects = projectRepository.searchByTitleOrTagsOrderByLikes(search, pageable);
                    } else {
                        projects = projectRepository.searchByTitleOrTags(search, pageable);
                    }
                }
            } else if (categoryIds != null) {
                // רק קטגוריה בלי חיפוש
                if ("popular".equals(sort)) {
                    projects = projectRepository.findByCategoryIdsOrderByLikes(categoryIds, pageable);
                } else {
                    projects = projectRepository.findByCategoryIds(categoryIds, pageable);
                }
            } else {
                // בלי חיפוש ובלי קטגוריה - הכל
                if ("popular".equals(sort)) {
                    projects = projectRepository.findAllOrderByLikesCountDesc(pageable);
                } else {
                    projects = sort.equals("oldest")
                            ? projectRepository.findAllByOrderByCreatedAtAsc(pageable)
                            : projectRepository.findAllByOrderByCreatedAtDesc(pageable);
                }
            }

            Page<ProjectListDTO> dtoPage = projectMapper.toProjectListDTOList(projects);
            return ResponseEntity.ok(dtoPage);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        }
    @GetMapping("/myProjects")
    public ResponseEntity<List<ProjectListDTO>> getProjectsByCurrentUser(Principal principal){
        try {
            List<Project> myProjects = projectRepository.findByUsers(getCurrentUser(principal));
            List<ProjectListDTO> myDTO = projectMapper.toProjectListDTOList(myProjects);
            return new ResponseEntity<>(myDTO,HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/{projectId}/assign-challenge/{challengeId}")
    public ResponseEntity<Project> assignToChallenge(
            @PathVariable Long projectId,
            @PathVariable Long challengeId) {

        Project project = projectRepository.findById(projectId).orElseThrow();
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow();

        project.setChallenge(challenge);
        projectRepository.save(project);

        return ResponseEntity.ok(project);
    }


}























