package com.example.diy.controller;


import com.example.diy.DTO.ProjectCreateDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.DTO.ProjectResponseDTO;
import com.example.diy.Mapper.ProjectMapper;
import com.example.diy.model.Project;
import com.example.diy.model.Tag;
import com.example.diy.model.Users;
import com.example.diy.service.ImageUtils;
import com.example.diy.service.ProjectRepository;
import com.example.diy.service.TagRepository;
import com.example.diy.service.UsersRepository;
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
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    ProjectRepository projectRepository;
    ProjectMapper projectMapper;
    UsersRepository usersRepository;
    TagRepository tagRepository;


    public ProjectController(ProjectRepository projectRepository, ProjectMapper projectMapper, UsersRepository usersRepository, TagRepository tagRepository) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.usersRepository = usersRepository;
        this.tagRepository = tagRepository;
    }

    @GetMapping("/getProject/{id}")
    public ResponseEntity<ProjectResponseDTO> get(@PathVariable long id) throws IOException {
        Project p = projectRepository.findById(id).get();
        if (p != null) return new ResponseEntity<>(projectMapper.projectToDTO(p), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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


    @GetMapping("/allProjects")
    public ResponseEntity<Page<ProjectListDTO>> getAllProjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        // יוצר בקשה: עמוד X, 30 פריטים, מיון לפי createdAt מהחדש לישן
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // מקבל את העמוד מה-DB
        Page<Project> projectPage = projectRepository.findAllByOrderByCreatedAtDesc(pageable);
        // ממיר כל Project ל-ProjectListDTO (עם MapStruct)
        Page<ProjectListDTO> dtoPage = projectPage.map(projectMapper::toProjectListDTO);
        // מחזיר ללקוח
        return ResponseEntity.ok(dtoPage);
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


//    @GetMapping("/projectByCategory")
//    public ResponseEntity<Map<Long, List<ProjectListDTO>>> getHomeProjects() {
//        Map<Long, List<ProjectListDTO>> homeProjects = homeService.getLatestProjectsPerCategory();
//        return ResponseEntity.ok(homeProjects);

//    }
}






















