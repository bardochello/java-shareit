package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

// Маппер для комментариев
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentMapper {

    Comment toComment(CommentCreateDto dto);

    @org.mapstruct.Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);
}
