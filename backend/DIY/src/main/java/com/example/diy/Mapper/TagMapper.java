package com.example.diy.Mapper;

import com.example.diy.DTO.TagDTO;
import com.example.diy.model.Tag;
import org.mapstruct.Mapper;

import java.util.Set;
@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDTO toDto(Tag tag);
    Set<TagDTO> toDtoSet(Set<Tag> tags);
}
