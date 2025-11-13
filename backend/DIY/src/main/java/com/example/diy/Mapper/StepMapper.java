package com.example.diy.Mapper;

import com.example.diy.DTO.StepDTO;
import com.example.diy.DTO.StepResponseDTO;
import com.example.diy.model.Step;
import com.example.diy.service.ImageUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StepMapper {
    Step stepDtoToStep(StepDTO stepDTO);

    StepResponseDTO stepEntityToResponseDTO(Step step);

    @AfterMapping
    default void handleStepPicture(@MappingTarget StepResponseDTO dto, Step step) {
        if (step.getPicturePath() != null) {
            try {
                dto.setPicture(ImageUtils.getImage(step.getPicturePath()));
            } catch (IOException e) {
                e.printStackTrace();
                dto.setPicture(null);
            }
        }
    }


    @Mapping(target = "id", ignore = true) // לא לשנות את ה-ID
    @Mapping(target = "project", ignore = true) // לא לשנות את הפרויקט
    void updateStepFromDto(StepDTO stepDTO, @MappingTarget Step step);

    StepDTO stepToStepDTO(Step step);

    List<StepResponseDTO> toDtoList(List<Step> steps);

//    @AfterMapping
//    default void handleProfilePicture(@MappingTarget StepDTO dto, Step step) {
//        if (step.getPicturePath() != null) {
//            try {
//                // כאן מטפלים ב־IOException במקום לזרוק אותו
//                String imageBase64 = ImageUtils.getImage(step.getPicturePath());
//                dto.setPicture(imageBase64);
//            } catch (IOException e) {
//                e.printStackTrace(); // או טיפול מותאם אחר
//                dto.setPicture(null); // במקרה של שגיאה
//            }
//        }
//    }

}
