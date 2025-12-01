package com.example.diy.Mapper;

import com.example.diy.DTO.ChallengeCreateDTO;
import com.example.diy.DTO.ChallengeListDTO;
import com.example.diy.DTO.ChallengeResponseDTO;
import com.example.diy.model.Challenge;
import com.example.diy.service.ImageUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                ProjectMapper.class // 🌟 הוסף את ה-ProjectMapper
                , UsersMapper.class // אם קיים ומשתמשים בו ישירות
        }
)
public interface ChallengeMapper {
    List<ChallengeListDTO> toChallengeListDTOList(List<Challenge> challenges);

    Challenge challengeCreateDTOToEntity(ChallengeCreateDTO challengeCreateDTO);

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


    @Mapping(target = "projects", source = "projects")
    @Mapping(target = "status", ignore = true)
    ChallengeResponseDTO toChallengeResponseDTO(Challenge challenge);

    @AfterMapping
    default void handleChallengeResponseDTO(@MappingTarget ChallengeResponseDTO dto, Challenge challenge) {
        // קביעת הסטטוס
        dto.setStatus(calculateChallengeStatus(challenge.getStartDate(), challenge.getEndDate()));

        // טיפול בתמונת האתגר (base64)
        if (challenge.getPicturePath() != null) {
            try {
                String imageBase64 = ImageUtils.getImage(challenge.getPicturePath());
                dto.setPicture(imageBase64);
            } catch (IOException e) {
                e.printStackTrace();
                dto.setPicture(null);
            }
        }
    }

    // מתודת עזר לקביעת הסטטוס
    default String calculateChallengeStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();

        if (today.isBefore(startDate)) {
            return "UPCOMING";
        } else if (today.isAfter(endDate)) {
            return "CLOSED";
        } else {
            return "OPEN";
        }
    }


}
