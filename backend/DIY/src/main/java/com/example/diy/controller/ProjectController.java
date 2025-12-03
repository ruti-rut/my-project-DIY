package com.example.diy.controller;

import com.example.diy.DTO.ProjectCreateDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.DTO.ProjectResponseDTO;
import com.example.diy.Mapper.ProjectMapper;
import com.example.diy.model.*;
import com.example.diy.security.CustomUserDetails;
import com.example.diy.service.*;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.BaseDirection;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.TextAlignment;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/project")
public class ProjectController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final EntityManager entityManager;
    ProjectRepository projectRepository;
    ProjectMapper projectMapper;
    UsersRepository usersRepository;
    TagRepository tagRepository;
    HomeService homeService;
    ChallengeRepository challengeRepository;
    StepRepository stepRepository;


    public ProjectController(EntityManager entityManager, StepRepository stepRepository, ChallengeRepository challengeRepository, HomeService homeService, TagRepository tagRepository, UsersRepository usersRepository, ProjectMapper projectMapper, ProjectRepository projectRepository) {
        this.entityManager = entityManager;
        this.stepRepository = stepRepository;
        this.challengeRepository = challengeRepository;
        this.homeService = homeService;
        this.tagRepository = tagRepository;
        this.usersRepository = usersRepository;
        this.projectMapper = projectMapper;
        this.projectRepository = projectRepository;
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

            Users currentUser = getCurrentUser(principal);
            project.setUsers(currentUser);
            project.setPicturePath(file.getOriginalFilename());

            Set<Tag> tags = new HashSet<>();
            List<String> tagNames = p.getTagNames();

            if (tagNames != null && !tagNames.isEmpty()) {
                List<Tag> existingTags = tagRepository.findByNameIn(tagNames);
                Set<String> existingNames = existingTags.stream().map(Tag::getName).collect(Collectors.toSet());
                tags.addAll(existingTags);

                tagNames.stream().filter(name -> !existingNames.contains(name)).forEach(name -> {
                    Tag newTag = new Tag();
                    newTag.setName(name);
                    tags.add(tagRepository.save(newTag));
                });
            }
            project.setTags(tags);
            if (p.getChallengeId() != null) {
                Challenge challenge = challengeRepository.findById(p.getChallengeId())
                        .orElseThrow(() -> new RuntimeException("Challenge not found"));
                project.setChallenge(challenge);
            }
            Project savedProject = projectRepository.save(project);
            ProjectResponseDTO responseDTO = projectMapper.projectEntityToResponseDTO(savedProject);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (IOException e) {
            System.out.println(e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProjectListDTO>> getProjectsByCategory(@PathVariable Long categoryId, Principal principal) {
        Users currentUser = principal != null ? getCurrentUser(principal) : null;

        List<Project> projects = projectRepository.findByCategoryId(categoryId);

        if (projects != null) {
            List<ProjectListDTO> dtos = projectMapper.toProjectListDTOList(projects, currentUser);
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @PutMapping("/editProject/{id}")
    @Transactional
    public ResponseEntity<ProjectResponseDTO> updateProjectWithImage(
            @PathVariable Long id,
            @RequestPart(value = "image", required = false) MultipartFile file,
            @RequestPart("project") ProjectCreateDTO p,
            Principal principal) {
        try {
            Project existingProject = projectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            Users currentUser = getCurrentUser(principal);
            if (!existingProject.getUsers().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }


            projectMapper.updateProjectFromDto(p, existingProject);

            if (file != null && !file.isEmpty()) {
                ImageUtils.uploadImage(file);
                existingProject.setPicturePath(file.getOriginalFilename());
            }

            if (p.getTagNames() != null) {
                Set<Tag> tags = new HashSet<>();
                List<Tag> existingTags = tagRepository.findByNameIn(p.getTagNames());
                Set<String> existingNames = existingTags.stream()
                        .map(Tag::getName)
                        .collect(Collectors.toSet());
                tags.addAll(existingTags);

                p.getTagNames().stream()
                        .filter(name -> !existingNames.contains(name))
                        .forEach(name -> {
                            Tag newTag = new Tag();
                            newTag.setName(name);
                            tags.add(tagRepository.save(newTag));
                        });

                existingProject.setTags(tags);
            }

            if (p.getChallengeId() != null) {
                Challenge challenge = challengeRepository.findById(p.getChallengeId())
                        .orElseThrow(() -> new RuntimeException("Challenge not found"));
                existingProject.setChallenge(challenge);
            } else {
                existingProject.setChallenge(null);
            }

            Project savedProject = projectRepository.save(existingProject);
            ProjectResponseDTO responseDTO = projectMapper.projectEntityToResponseDTO(savedProject);

            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{projectId}/favorite")
    @Transactional
    public ResponseEntity<Void> addToFavorites(@PathVariable Long projectId,
                                               Principal principal) {
        Users currentUser = getCurrentUser(principal);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!currentUser.getFavoriteProjects().contains(project)) {
            currentUser.getFavoriteProjects().add(project);
            usersRepository.save(currentUser);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/favorite")
    @Transactional
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long projectId, Principal principal) {
        Users currentUser = getCurrentUser(principal);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        currentUser.getFavoriteProjects().remove(project);

        usersRepository.save(currentUser);

        return ResponseEntity.ok().build();
    }

    private Users getCurrentUser(Principal principal) {
        String username = principal.getName(); // מהטוקן
        return usersRepository.findByUserName(username);
    }


    @GetMapping("/allProjects")
    public ResponseEntity<Page<ProjectListDTO>> getAllProjectsWithFilters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(defaultValue = "newest") String sort,
            Principal principal
    ) {
        logger.info("--- STARTING PROJECTS FETCH ---");
        logger.info("Received Params: Page={}, Size={}, Sort={}, Search='{}', Categories={}",
                page, size, sort, search, categoryIds);

        try {
            Users currentUser = principal != null ? getCurrentUser(principal) : null;
            Page<Project> projects;
            Pageable pageable;

            String usedSort;
            Specification<Project> publicSpec = Specification.where(ProjectSpecifications.search(search))
                    .and(ProjectSpecifications.categoryIn(categoryIds))
                    .and(ProjectSpecifications.isNotDraft()); // ✅ תנאי חדש!

            if ("popular".equals(sort)) {
                usedSort = "CUSTOM POPULAR (findPopularProjects)"; // הגדרת שם המיון

                pageable = PageRequest.of(page, size);
                projects = projectRepository.findPopularProjects(search, categoryIds, pageable);

            } else {

                usedSort = sort; // השארת שם המיון כפי שהגיע (newest/oldest)

                Sort sortObj = "oldest".equals(sort)
                        ? Sort.by("createdAt").ascending()
                        : Sort.by("createdAt").descending(); // ברירת מחדל newest

                pageable = PageRequest.of(page, size, sortObj);

                Specification<Project> spec = Specification.where(ProjectSpecifications.search(search))
                        .and(ProjectSpecifications.categoryIn(categoryIds));

                projects = projectRepository.findAll(publicSpec, pageable);
            }

            logger.info("Using DB Query: {} strategy. Total records found: {}", usedSort, projects.getTotalElements());

            logger.info("Query successful. Returning {} projects on page {}.", projects.getNumberOfElements(), projects.getNumber());
            Page<ProjectListDTO> dtoPage = projectMapper.toProjectListDTOList(projects, currentUser);
            return ResponseEntity.ok(dtoPage);

        } catch (Exception e) {
            logger.error("Error fetching projects:", e); // הדפסה מלאה של השגיאה ללוג
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/myProjects/published") // ✅ נתיב חדש לפרויקטים שפורסמו (isDraft = false)
    public ResponseEntity<List<ProjectListDTO>> getMyPublishedProjects(Principal principal) {
        try {
            Users currentUser = getCurrentUser(principal);
            if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            // קבל רק פרויקטים שפורסמו
            List<Project> publishedProjects = projectRepository.findByUsersAndIsDraft(currentUser, false);

            List<ProjectListDTO> dtos = projectMapper.toProjectListDTOList(publishedProjects, currentUser);
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/myProjects/drafts")
    public ResponseEntity<List<ProjectListDTO>> getMyDraftProjects(Principal principal) {
        try {
            Users currentUser = getCurrentUser(principal);
            if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            // קבל רק טיוטות
            List<Project> draftProjects = projectRepository.findByUsersAndIsDraft(currentUser, true);

            List<ProjectListDTO> dtos = projectMapper.toProjectListDTOList(draftProjects, currentUser);
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/myFavorites")
    public ResponseEntity<List<ProjectListDTO>> getMyFavorites(Principal principal) {
        try {
            Users currentUser = getCurrentUser(principal);
            if (currentUser == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            List<Project> favoritesList = new ArrayList<>(currentUser.getFavoriteProjects());
            List<ProjectListDTO> dtos = projectMapper.toProjectListDTOList(favoritesList, currentUser);
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{projectId}/assign-challenge/{challengeId}")
    public ResponseEntity<Void> assignToChallenge(
            @PathVariable Long projectId,
            @PathVariable Long challengeId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Users currentUser = userDetails.getUser();
        Long currentUserId = userDetails.getId();

        if (projectRepository.existsByChallengeIdAndUsersId(challengeId, currentUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User already submitted a project to this challenge."
            );
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found."));

        if (!project.getUsers().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the owner of the project.");
        }

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found."));

        project.setChallenge(challenge);
        project.setDraft(false);
        projectRepository.save(project);

        return ResponseEntity.ok().build();
    }



    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateProjectPdf(@PathVariable Long id) throws Exception {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // ייתכן ותרצי להשתמש ב-project.getSteps() אם קיים קשר @OneToMany
        List<Step> steps = stepRepository.findByProjectId(id);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);

        // **תיקון 1:** יצירת אובייקט Document הנכון של iText
        Document document = new Document(pdf);

        // ----- תמיכה בעברית ופונט -----
        // **תיקון 2:** טעינת הפונט. ודאי שקובץ 'arial.ttf' קיים בנתיב היחסי או המוחלט.
        // אם השגיאה עדיין קיימת, בדקי את גרסת ה-iText ואת המיקום של הקובץ.
        PdfFont font = PdfFontFactory.createFont("fonts/arial.ttf", PdfEncodings.IDENTITY_H);

        document.setFont(font);
        document.setProperty(Property.BASE_DIRECTION, BaseDirection.RIGHT_TO_LEFT);

        // ----- כותרת, תיאור, תמונה ראשית -----
        Paragraph title = new Paragraph(project.getTitle())
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        if (project.getDescription() != null) {
            document.add(new Paragraph(project.getDescription()).setFontSize(14));
        }

        if (project.getPicturePath() != null) {
            String coverPath = System.getProperty("user.dir") + "/images/" + project.getPicturePath();
            try {
                ImageData imgData = ImageDataFactory.create(coverPath);
                // ממורכז את התמונה
                Image img = new Image(imgData).setWidth(350).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                document.add(img);
            } catch (IOException e) {
                System.err.println("Error loading cover image: " + coverPath + " - " + e.getMessage());
            }
        }

        // ----- שלבים -----
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("שלבי העבודה").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("\n"));

        for (Step step : steps) {

            document.add(new Paragraph("שלב " + step.getStepNumber() + ": " + step.getTitle())
                    .setBold()
                    .setFontSize(16));

            document.add(new Paragraph(step.getContent()).setFontSize(12));

            // תמונת שלב
            if (step.getPicturePath() != null) {

                String imgPath = System.getProperty("user.dir")
                        + "/images/" + step.getPicturePath();

                try {
                    ImageData imgData2 = ImageDataFactory.create(imgPath);
                    Image img2 = new Image(imgData2).setWidth(300).setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);
                    document.add(img2);
                } catch (IOException e) {
                    System.err.println("Error loading step image: " + imgPath + " - " + e.getMessage());
                }
            }
            document.add(new Paragraph("\n"));
        }

        document.close();

        // החזרת ה-PDF כבייט-אייריי. שם הקובץ נשלח ב-Content-Disposition.
        return ResponseEntity.ok()
                // שם הקובץ הדינמי:
                .header("Content-Disposition", "attachment; filename=\"" + project.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(baos.toByteArray());
    }


    @DeleteMapping("/deleteProject/{id}")
    @Transactional // 🔥 חובה! כדי שפעולות הניקוי והמחיקה יפעלו יחד
    public ResponseEntity<Void> deleteProject(@PathVariable Long id, Principal principal) {
        try {
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            Users currentUser = getCurrentUser(principal);
            if (!project.getUsers().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            projectRepository.clearFavoriteProjectsJoinTable(id);
            projectRepository.clearLikedProjectsJoinTable(project.getId());

            projectRepository.delete(project);


            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{projectId}/like")
    @Transactional
    public ResponseEntity<Void> likeProject(@PathVariable Long projectId, Principal principal) {
        Users currentUser = getCurrentUser(principal);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.getLikedByUsers().add(currentUser);
        projectRepository.save(project);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{projectId}/like")
    @Transactional
    public ResponseEntity<Void> unlikeProject(@PathVariable Long projectId, Principal principal) {
        Users currentUser = getCurrentUser(principal);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.getLikedByUsers().remove(currentUser);
        projectRepository.save(project);

        return ResponseEntity.ok().build();
    }

}























