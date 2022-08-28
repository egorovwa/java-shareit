package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.Entitys.COMMENTID1_USER2;
import static ru.practicum.shareit.Entitys.USER_ID2;

class CommentDtoMaperTest {
    CommentDtoMaper commentDtoMaper = new CommentDtoMaper();

    @Test
    void toDto() {
        Comment comment = COMMENTID1_USER2;
        CommentDto dto = new CommentDto(1L, COMMENTID1_USER2.getText(),
                LocalDateTime.ofEpochSecond(COMMENTID1_USER2.getCreated(), 0, ZoneOffset.UTC),
                USER_ID2.getName());
        assertThat(commentDtoMaper.toDto(comment), is(dto));

    }
}