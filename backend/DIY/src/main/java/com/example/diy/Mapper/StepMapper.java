package com.example.diy.Mapper;

import com.example.diy.DTO.StepDTO;
import com.example.diy.model.Step;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StepMapper {

    Step stepDTOToStep(StepDTO dto);

}
