package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CommentDtoMaper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(),
                LocalDateTime.ofEpochSecond(comment.getCreated(), 0, ZoneOffset.UTC),
                comment.getAuthor().getName());
    }
}
