package com.example.diy.Mapper;

import com.example.diy.DTO.ChallengeListDTO;
import com.example.diy.DTO.ProjectCreateDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.model.Challenge;
import com.example.diy.model.Project;
import com.example.diy.service.ImageUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.io.IOException;
import java.util.List;
@Mapper(componentModel = "spring")
public interface ChallengeMapper {
    List<ChallengeListDTO> toChallengeListDTOList(List<Challenge> challenges);

    @Mapping(target = "picture", ignore = true)
    ChallengeListDTO toChallengeListDTO(Challenge c);
    @AfterMapping
    default void handleProfilePicture(@MappingTarget ChallengeListDTO dto, Challenge challenge) {
        if (challenge.getPicturePath() != null) {
            try {
                // כאן מטפלים ב־IOException במקום לזרוק אותו
                String imageBase64 = ImageUtils.getImage(challenge.getPicturePath());
                dto.setPicture(imageBase64);
            } catch (IOException e) {
                e.printStackTrace(); // או טיפול מותאם אחר
                dto.setPicture(null); // במקרה של שגיאה
            }
        }
    }

}
