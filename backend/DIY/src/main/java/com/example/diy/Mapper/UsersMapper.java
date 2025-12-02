package com.example.diy.Mapper;

import com.example.diy.DTO.UserProfileDTO;
import com.example.diy.DTO.UserResponseDTO;
import com.example.diy.DTO.UsersRegisterDTO;
import com.example.diy.DTO.UsersSimpleDTO;
import com.example.diy.model.Users;
import com.example.diy.service.ImageUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    // --- Basic Mappings ---
    @Mapping(target = "profilePicture", ignore = true)
    UserResponseDTO usersToUserResponseDTO(Users users);

    Users usersRegisterDTOToUsers(UsersRegisterDTO usersRegisterDTO);

    // --- Complex Mappings with Custom Logic ---

    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "projectsCount", expression = "java(users.getMyProjects() != null ? users.getMyProjects().size() : 0)")
    @Mapping(target = "favoritesCount", expression = "java(users.getFavoriteProjects() != null ? users.getFavoriteProjects().size() : 0)")
    UserProfileDTO usersToUserProfileDTO(Users users);

    @Mapping(target = "profilePicture", ignore = true)
    UsersSimpleDTO toSimpleDTO(Users user);


    @AfterMapping
    default void handleProfilePicture(@MappingTarget Object dto, Users user) {
        if(user==null)
            return;
        if (user.getProfilePicturePath() == null || user.getProfilePicturePath().trim().isEmpty()) {
            return;
        }

        try {
            String base64 = ImageUtils.getImage(user.getProfilePicturePath());

            if (dto instanceof UserProfileDTO profileDto) {
                profileDto.setProfilePicture(base64);
                profileDto.setProfilePicturePath("/images/" + user.getProfilePicturePath());
            }
            if (dto instanceof UsersSimpleDTO simpleDto) {
                simpleDto.setProfilePicture(base64);
            }
            if (dto instanceof UserResponseDTO responseDto) {
                responseDto.setProfilePicture(base64);  // ← השורה הזו חסרה!!!
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}







// --- After Mapping Logic (Default Methods) ---

//    @AfterMapping
//    default void handleProfilePicture(@MappingTarget UserProfileDTO dto, Users user) {
//        // בדיוק אותו קוד כמו שיש לך
//        if (user.getProfilePicturePath() != null && !user.getProfilePicturePath().trim().isEmpty()) {
//            try {
//                String base64 = ImageUtils.getImage(user.getProfilePicturePath());
//                dto.setProfilePicture(base64);  // חשוב: בלי "data:image/..." – כי אתה מוסיף את זה באנגולר!
//                dto.setProfilePicturePath("/images/" + user.getProfilePicturePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//                dto.setProfilePicture(null);
//                dto.setProfilePicturePath(null);
//            }
//        }
//    }
//    @AfterMapping
//    default void handleProfilePicture(@MappingTarget UsersSimpleDTO dto, Users user) {
//        if (user.getProfilePicturePath() != null) {
//            try {
//                String imageBase64 = ImageUtils.getImage(user.getProfilePicturePath());
//                dto.setProfilePicture(imageBase64);
//            } catch (IOException e) {
//                e.printStackTrace();
//                dto.setProfilePicture(null);
//            }
//        }
//    }
//}
//    default UsersSimpleDTO toSimpleDTO(Users user) throws IOException {
//        if (user == null) return null;
//
//        UsersSimpleDTO usersSimpleDTO = new UsersSimpleDTO();
//        usersSimpleDTO.setId(user.getId());
//        usersSimpleDTO.setUserName(user.getUserName());
//        usersSimpleDTO.setProfilePicturePath(user.getProfilePicturePath());
//
//        Path filename = Paths.get(user.getProfilePicturePath());//לוקח את הנתיב של התמונה
//        byte[] byteImage = Files.readAllBytes(filename);//מעביר אותה למערך של ביטים
//        //כדי להפחית את תעבורת הרשת, נקודד למחרוזת של base64 שהיא קטנה יותר
//        usersSimpleDTO.setProfilePicture(Base64.getEncoder().encodeToString(byteImage));
//
//        return usersSimpleDTO;
//    }

//    default Users toEntityFromSimpleDTO(UsersSimpleDTO dto) {
//        Users users = new Users();
//        users.setId(dto.getId());
//        users.setUserName(dto.getUserName());
//        users.setProfilePicturePath(dto.getProfilePicturePath());
//        return users;
//    }



