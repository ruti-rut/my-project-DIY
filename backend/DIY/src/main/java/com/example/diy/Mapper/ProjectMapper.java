package com.example.diy.Mapper;

import com.example.diy.DTO.ProjectCreateDTO;
import com.example.diy.DTO.ProjectResponseDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UsersMapper.class, CategoryMapper.class})
public interface ProjectMapper {

    ProjectResponseDTO projectEntityToResponseDTO(Project entity);

    @Mapping(source = "users", target = "users")
    @Mapping(source = "category", target = "category")
    ProjectResponseDTO projectToDTO(Project project);

    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "users", ignore = true)
    Project projectCreateDTOToEntity (ProjectCreateDTO dto);

    @Mapping(source = "users", target = "usersSimpleDTO")
    ProjectListDTO toProjectListDTO(Project project);

    default Page<ProjectListDTO> toProjectListDTOList(Page<Project> projects) {
        return projects.map(this::toProjectListDTO);
    }

    List<ProjectListDTO> toProjectListDTOList(List<Project> projects);

    @Mapping(target = "id", ignore = true) // מוודא ששדה ה-ID של הישות לא יידרס
    // נניח שאת צריכה לעדכן את הקטגוריה והאתגר באמצעות ה-IDs שלהם
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "challengeId", target = "challenge.id")
        // ניתן להוסיף כאן לוגיקה מורכבת יותר, למשל לתגיות
    Project updateProjectFromDto(ProjectCreateDTO p, @MappingTarget Project existingProject);

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
