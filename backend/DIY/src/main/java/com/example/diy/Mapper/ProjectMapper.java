package com.example.diy.Mapper;

import com.example.diy.DTO.ProjectCreateDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.DTO.ProjectResponseDTO;
import com.example.diy.model.Project;
import com.example.diy.service.ImageUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                UsersMapper.class,
                CategoryMapper.class,
                StepMapper.class,
                CommentMapper.class,
                TagMapper.class
        }

)
public interface ProjectMapper {

    @Mapping(target = "users", source = "users")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "steps", source = "steps")        // חובה!
    @Mapping(target = "comments", source = "comments")  // חובה!
    @Mapping(target = "tags", source = "tags")          // חובה!
    @Mapping(target = "likesCount", expression = "java(entity.getLikedByUsers() != null ? entity.getLikedByUsers().size() : 0)")
    ProjectResponseDTO projectEntityToResponseDTO(Project entity);

    @AfterMapping
    default void handleProjectPictureForResponse(@MappingTarget ProjectResponseDTO dto, Project project) {
        if (project.getPicturePath() != null) {
            try {
                dto.setPicture(ImageUtils.getImage(project.getPicturePath()));
            } catch (IOException e) {
                e.printStackTrace();
                dto.setPicture(null);
            }
        }
    }


    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "challengeId", target = "challenge.id")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "users", ignore = true)
    Project projectCreateDTOToEntity(ProjectCreateDTO dto);


    default Page<ProjectListDTO> toProjectListDTOList(Page<Project> projects) {
        return projects.map(this::toProjectListDTO);
    }

    List<ProjectListDTO> toProjectListDTOList(List<Project> projects);

    @Mapping(target = "id", ignore = true) // מוודא ששדה ה-ID של הישות לא יידרס
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "challengeId", target = "challenge.id")
    Project updateProjectFromDto(ProjectCreateDTO p, @MappingTarget Project existingProject);

    @Mapping(
            target = "challengeId",
            expression = "java(project.getChallenge() != null ? project.getChallenge().getId() : null)"
    )
    @Mapping(source = "users", target = "usersSimpleDTO")
    @Mapping(target = "picture", ignore = true)
    ProjectListDTO toProjectListDTO(Project project);

    // לוגיקה מותאמת אישית לטיפול בשדה התמונה
    @AfterMapping
    default void handleProjectPicture(@MappingTarget ProjectListDTO dto, Project project) {
        // ודא שאתה ממלא את שדה ה-'picture' ב-DTO
        if (project.getPicturePath() != null) {
            try {
                // קורא את הקובץ וממיר ל-Base64
                String imageBase64 = ImageUtils.getImage(project.getPicturePath());
                dto.setPicture(imageBase64);
            } catch (IOException e) {
                // הדפסת שגיאה ודאית לתהליך, והשארת picture כ-null או ריק
                e.printStackTrace();
                dto.setPicture(null);
            }
        }
    }

    ProjectCreateDTO projectCreateToDTO(Project project);

//    @AfterMapping
//    default void handleProfilePicture(@MappingTarget ProjectCreateDTO dto, Project project) {
//        if (project.getPicturePath() != null) {
//            try {
//                // כאן מטפלים ב־IOException במקום לזרוק אותו
//                String imageBase64 = ImageUtils.getImage(project.getPicturePath());
//                dto.setPicture(imageBase64);
//            } catch (IOException e) {
//                e.printStackTrace(); // או טיפול מותאם אחר
//                dto.setPicture(null); // במקרה של שגיאה
//            }
//        }
//    }


//    default ProjectCreateDTO ProjectCreateToDTO(Project project, CategoryMapper cm) {
//        ProjectCreateDTO projectCreateDTO = new ProjectCreateDTO();
//        projectCreateDTO.setCategory(cm.categoryToDTO(project.getCategory()));
//        projectCreateDTO.setChallenge(project.getChallenge());
//        projectCreateDTO.setSteps(project.getSteps());
//        projectCreateDTO.setTags(project.getTags());
//        projectCreateDTO.setMaterials(project.getMaterials());
//        projectCreateDTO.setTitle(project.getTitle());
//        projectCreateDTO.setAges(project.getAges());
//        projectCreateDTO.setTimePrep(project.getTimePrep());
//        projectCreateDTO.setPicture(project.getPicturePath());
//        projectCreateDTO.setDescription(project.getDescription());
//        projectCreateDTO.setDraft(project.isDraft());
//        return projectCreateDTO;
//    }
}
