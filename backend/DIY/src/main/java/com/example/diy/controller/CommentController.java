package com.example.diy.controller;

import com.example.diy.DTO.CommentCreateDTO;
import com.example.diy.DTO.CommentDTO;
import com.example.diy.Mapper.CommentMapper;
import com.example.diy.model.Comment;
import com.example.diy.model.Project;
import com.example.diy.model.Users;
import com.example.diy.service.CommentRepository;
import com.example.diy.service.ProjectRepository;
import com.example.diy.service.UsersRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private CommentRepository commentRepository;
    private ProjectRepository projectRepository;
    private UsersRepository usersRepository;
    private CommentMapper commentMapper;

    public CommentController(CommentRepository commentRepository, ProjectRepository projectRepository, UsersRepository usersRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.projectRepository = projectRepository;
        this.usersRepository = usersRepository;
        this.commentMapper = commentMapper;
    }

    @PostMapping("/addComment")
    public ResponseEntity<CommentDTO> addComment(
            @RequestBody @Valid CommentCreateDTO  dto,
            Principal principal) {
        try {
            // 1. בדיקה: האם המשתמש מחובר?
            if (principal == null || principal.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // 2. בדיקה: האם התוכן לא ריק?
            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            // 3. מציאת המשתמש
            Users currentUser = usersRepository.findByUserName(principal.getName());
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            // 4. מציאת הפרויקט - חסר קריאה לפרויקט, נניח שהוא מגיע ב-DTO

            // בדיקה שדות קריטיים נוספים (אם נדרש)
            if (dto.getProjectId() == null) {
                return ResponseEntity.badRequest().build();
            }

            // נשלים את מציאת הפרויקט
            Project project = projectRepository.findById(dto.getProjectId())
                    .orElseThrow(() -> new RuntimeException("Project not found"));


            // 5. יצירת ההערה
            Comment comment = commentMapper.commentCreateDTOtoEntity(dto);
            comment.setContent(dto.getContent().trim());
            comment.setUser(currentUser);
            comment.setProject(project); // קישור הפרויקט שנמצא

            // createdAt יתווסף אוטומטית
            // 6. שמירה במסד
            Comment saved = commentRepository.save(comment);

            // 7. החזרת DTO
            return ResponseEntity.ok(commentMapper.commentToDTO(saved));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Project not found")) {
                return ResponseEntity.notFound().build();
            }
            // לוג של השגיאה (חשוב!)
            System.err.println("Error adding comment: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            // לוג של השגיאה (חשוב!)
            System.err.println("Error adding comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/project/{projectId}/comments")
    public ResponseEntity<Page<CommentDTO>> getPagedComments(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            // בדיקה אם הפרויקט קיים (אופציונלי אך מומלץ)
            if (!projectRepository.existsById(projectId)) {
                return ResponseEntity.notFound().build();
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Comment> commentPage = commentRepository.findByProjectIdOrderByCreatedAtDesc(projectId, pageable);

            return ResponseEntity.ok(commentMapper.toDtoPage(commentPage));

        } catch (Exception e) {
            System.err.println("Error fetching comments for project " + projectId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}