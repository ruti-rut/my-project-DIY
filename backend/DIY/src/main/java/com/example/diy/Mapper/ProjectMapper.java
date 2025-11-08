package com.example.diy.Mapper;

import com.example.diy.DTO.ProjectCreateDTO;
import com.example.diy.DTO.ProjectDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.model.Project;
import com.example.diy.service.ImageUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UsersMapper.class, CategoryMapper.class})
public interface ProjectMapper {

    @Mapping(source = "users", target = "users")
    @Mapping(source = "category", target = "category")
    ProjectDTO projectToDTO(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "picturePath", source = "picturePath")
    Project projectCreateDTOToEntity (ProjectCreateDTO dto);

    @Mapping(source = "users", target = "usersSimpleDTO")
    ProjectListDTO toProjectListDTO(Project project);

    List<ProjectListDTO> toProjectListDTOList(List<Project> projects);


    @Mapping(target = "picture", ignore = true)
    ProjectCreateDTO projectCreateToDTO(Project project);

    @AfterMapping
    default void handleProfilePicture(@MappingTarget ProjectCreateDTO dto, Project project) {
        if (project.getPicturePath() != null) {
            try {
                // כאן מטפלים ב־IOException במקום לזרוק אותו
                String imageBase64 = ImageUtils.getImage(project.getPicturePath());
                dto.setPicture(imageBase64);
            } catch (IOException e) {
                e.printStackTrace(); // או טיפול מותאם אחר
                dto.setPicture(null); // במקרה של שגיאה
            }
        }
    }


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
