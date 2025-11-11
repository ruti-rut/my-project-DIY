package com.example.diy.controller;

import com.example.diy.DTO.ProjectCreateDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.DTO.ProjectResponseDTO;
import com.example.diy.Mapper.ProjectMapper;
import com.example.diy.model.Project;
import com.example.diy.model.Users;
import com.example.diy.service.HomeService;
import com.example.diy.service.ImageUtils;
import com.example.diy.service.ProjectRepository;
import com.example.diy.service.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    ProjectRepository projectRepository;
    ProjectMapper projectMapper;
    UsersRepository usersRepository;


    public ProjectController(ProjectRepository projectRepository, ProjectMapper projectMapper, UsersRepository usersRepository) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.usersRepository = usersRepository;
    }

    @GetMapping("/getProject/{id}")
    public ResponseEntity<ProjectResponseDTO> get(@PathVariable long id) throws IOException {
        Project p = projectRepository.findById(id).get();
        if (p != null)
            return new ResponseEntity<>(projectMapper.projectToDTO(p), HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/uploadProject")
    public ResponseEntity<ProjectResponseDTO> uploadProjectWithImage(@RequestPart("image") MultipartFile file
            , @RequestPart("project") ProjectCreateDTO p,
              Principal principal) {
        try {
            ImageUtils.uploadImage(file);
            Project project = projectMapper.projectCreateDTOToEntity(p);

// 1. מציאת שם המשתמש (או המייל) מה-Principal
            String username = principal.getName();

            // 2. מציאת ישות המשתמש המלאה מה-DB
            Users currentUser = usersRepository.findByUserName(username);

            // 3. קישור הפרויקט למשתמש
            project.setUsers(currentUser); // <--- הוסף את השורה הזו!            project.setPicturePath(file.getOriginalFilename());


            Project savedProject = projectRepository.save(project);
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

    //    @GetMapping("/projectByCategory")
//    public ResponseEntity<Map<Long, List<ProjectListDTO>>> getHomeProjects() {
//        Map<Long, List<ProjectListDTO>> homeProjects = homeService.getLatestProjectsPerCategory();
//        return ResponseEntity.ok(homeProjects);
//    }

    @GetMapping("/allProjects")
    public ResponseEntity<List<ProjectListDTO>> getAllChallenges() {
        List<Project> list = projectRepository.findAll();
        if (list != null) {
            return new ResponseEntity<>(projectMapper.toProjectListDTOList(list), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/editProject/{id}")
    public ResponseEntity<Project> updateProjectWithImage(@PathVariable Long id,
                                                          @RequestPart(value = "image", required = false) MultipartFile file,
                                                          @RequestPart("project") ProjectCreateDTO p) {
        try {
            // 1. קבלת הפרויקט הקיים מה-DB
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

}











