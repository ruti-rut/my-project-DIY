package com.example.diy.Mapper;

import com.example.diy.DTO.CommentCreateDTO;
import com.example.diy.DTO.CommentDTO;
import com.example.diy.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",uses = { UsersMapper.class })

public interface CommentMapper {
 @Mapping(target = "user", source = "user")
 CommentDTO commentToDTO(Comment comment);


 @Mapping(target = "user", ignore = true)     // ← נוסיף ידנית
 @Mapping(target = "createdAt", ignore = true) // ← @CreationTimestamp
 @Mapping(target = "id", ignore = true)
 @Mapping(target = "project.id", source = "projectId")
// ← Auto-generated
 Comment commentCreateDTOtoEntity(CommentCreateDTO dto);

 List<CommentDTO> commentsToDTOs(List<Comment> comments);
 default Page<CommentDTO> toDtoPage(Page<Comment> page) {
  if (page == null) return null;
  return page.map(this::commentToDTO);
 }
}
