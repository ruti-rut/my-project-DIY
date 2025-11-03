package com.example.diy.Mapper;

import com.example.diy.DTO.ChallengeListDTO;
import com.example.diy.DTO.ProjectListDTO;
import com.example.diy.model.Challenge;
import com.example.diy.model.Project;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring", uses = {UsersMapper.class, CategoryMapper.class})
public interface ChallengeMapper {
    ChallengeListDTO toChallengeListDTO(Challenge c);
    List<ChallengeListDTO> toChallengeListDTOList(List<Challenge> challenges);

}
