package com.example.diy.Mapper;

import com.example.diy.DTO.StepDTO;
import com.example.diy.model.Step;
import com.example.diy.service.ImageUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface StepMapper {

    Step stepDtoToStep(StepDTO stepDTO);
    @Mapping(target = "picture", ignore = true)
    StepDTO stepToStepDTO(Step step);

    @AfterMapping
    default void handleProfilePicture(@MappingTarget StepDTO dto, Step step) {
        if (step.getPicturePath() != null) {
            try {
                // כאן מטפלים ב־IOException במקום לזרוק אותו
                String imageBase64 = ImageUtils.getImage(step.getPicturePath());
                dto.setPicture(imageBase64);
            } catch (IOException e) {
                e.printStackTrace(); // או טיפול מותאם אחר
                dto.setPicture(null); // במקרה של שגיאה
            }
        }
    }

}
