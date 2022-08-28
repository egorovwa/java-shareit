package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMaper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Entitys.*;

@WebMvcTest(ItemController.class)
@Import({ItemDtoMaper.class, CommentDtoMaper.class, BookingDtoMaper.class})
class ItemControllerTest {

    @MockBean
    ItemServise itemServise;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    ItemDtoMaper dtoMaper = new ItemDtoMaper(new BookingDtoMaper());
    DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;




    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();

    }

    @Test
    void test1_1createItem_incorrectUserId() throws Exception {
        ItemDto itemDto = new ItemDto(null, ITEM_ID1_OWNER1_AVALIBLE_TRUE.getName(),
                ITEM_ID1_OWNER1_AVALIBLE_TRUE.getDescription(),
                true, null, null);
        when(itemServise.createItem(1, itemDto))
                .thenThrow(new IncorrectUserIdException("message", "id"));
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(IncorrectUserIdException.class)));

    }

    @Test
    void test1_2createItem_RequestNotExistException() throws Exception {
        ItemDto itemDto = new ItemDto(null, ITEM_ID1_OWNER1_AVALIBLE_TRUE.getName(),
                ITEM_ID1_OWNER1_AVALIBLE_TRUE.getDescription(),
                true, null, 1L);
        when(itemServise.createItem(1, itemDto))
                .thenThrow(new RequestNotExistException("message", "param", "val"));
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(RequestNotExistException.class)))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(),
                        is("message")));

    }

    @Test
    void test2_1_patchItem_ModelNotExitsException() throws Exception {
        ItemDto itemDto = new ItemDto(null, ITEM_ID1_OWNER1_AVALIBLE_TRUE.getName(),
                ITEM_ID1_OWNER1_AVALIBLE_TRUE.getDescription(),
                true, null, 1L);
        Item inItem = dtoMaper.fromDto(itemDto);
        when(itemServise.patchItem(1, 1, inItem))
                .thenThrow(new ModelNotExitsException("message", "param", "val"));
        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(ModelNotExitsException.class)));
    }

    @Test
    void test2_2_patchItem_IncorectUserOrItemIdException() throws Exception {
        ItemDto itemDto = new ItemDto(null, ITEM_ID1_OWNER1_AVALIBLE_TRUE.getName(),
                ITEM_ID1_OWNER1_AVALIBLE_TRUE.getDescription(),
                true, null, 1L);
        Item inItem = dtoMaper.fromDto(itemDto);
        when(itemServise.patchItem(1, 1, inItem))
                .thenThrow(new IncorectUserOrItemIdException("message", 1L, 1L));
        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(IncorectUserOrItemIdException.class)));
    }

    @Test
    void test3_findById_ModelNotExitsException() throws Exception {
        when(itemServise.findById(1L, 1L))
                .thenThrow(new ModelNotExitsException("messsage", "id", "value"));
        mvc.perform(get("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(ModelNotExitsException.class)));

    }

    @Test
    void test4_1_addComment_ModelNotFound() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(COMMENTID1_USER2.getText());
        Comment comment = COMMENTID1_USER2;
        when(itemServise.addComment(1L, 2L, COMMENTID1_USER2.getText()))
                .thenThrow(new ModelNotExitsException("message", "id", "val"));
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(ModelNotExitsException.class)));
    }

    @Test
    void test4_2_addComment_NotUsedCommentException() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(COMMENTID1_USER2.getText());
        Comment comment = COMMENTID1_USER2;
        when(itemServise.addComment(1L, 2L, COMMENTID1_USER2.getText()))
                .thenThrow(new NotUsedCommentException("message", 2L, 1L));
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(NotUsedCommentException.class)));
    }

    @Test
    void test4_3_addComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(COMMENTID1_USER2.getText());
        Comment comment = COMMENTID1_USER2;
        when(itemServise.addComment(1L, 2L, COMMENTID1_USER2.getText()))
                .thenReturn(comment);
        mvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authorName", is(USER_ID2.getName())))
                .andExpect(jsonPath("$.created",
                        is(timeFormatter.format(LocalDateTime
                                .ofEpochSecond(COMMENTID1_USER2.getCreated(), 0, ZoneOffset.UTC)))));
    }
}