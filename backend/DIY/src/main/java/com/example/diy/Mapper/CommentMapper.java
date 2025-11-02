package com.example.diy.Mapper;

import com.example.diy.DTO.CommentDTO;
import com.example.diy.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface CommentMapper {
 CommentDTO commentToDTO(Comment comment);
}
