package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.NotUsedCommentException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoMaper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.PageParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.TestConstants.*;

@WebMvcTest(ItemController.class)
@Import({ItemDtoMaper.class, CommentDtoMaper.class, BookingDtoMaper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ItemControllerTest {

    final ItemDtoMaper dtoMaper = new ItemDtoMaper(new BookingDtoMaper());
    final CommentDtoMaper commentDtoMaper = new CommentDtoMaper();
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @MockBean
    ItemServise itemServise;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;

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
    void test1_3createItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, ITEM_ID1_OWNER1_AVALIBLE_TRUE.getName(),
                ITEM_ID1_OWNER1_AVALIBLE_TRUE.getDescription(),
                true, null, 1L);
        when(itemServise.createItem(1, itemDto))
                .thenReturn(ITEM_ID1_OWNER1_AVALIBLE_TRUE);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(jsonPath("$.id", is(1)));
        Mockito.verify(itemServise, Mockito.times(1)).createItem(1L, itemDto);
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
    void test2_3_patchItem() throws Exception {
        ItemDto itemDto = new ItemDto(null, ITEM_ID1_OWNER1_AVALIBLE_TRUE.getName(),
                ITEM_ID1_OWNER1_AVALIBLE_TRUE.getDescription(),
                true, null, 1L);
        Item inItem = dtoMaper.fromDto(itemDto);
        when(itemServise.patchItem(1, 1, inItem))
                .thenReturn(ITEM_ID1_OWNER1_AVALIBLE_TRUE);
        mvc.perform(patch("/items/{itemId}", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(jsonPath("$.id", is(1)));
        Mockito.verify(itemServise, Mockito.times(1))
                .patchItem(1L, 1L, dtoMaper.fromDto(itemDto));

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
    void test3_1_findById() throws Exception {
        when(itemServise.findById(1L, 1L))
                .thenReturn(dtoMaper.toDtoWithBooking(ITEM_ID1_OWNER1_AVALIBLE_TRUE,
                        List.of(commentDtoMaper.toDto(COMMENTID1_USER2))));
        mvc.perform(get("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void test3_2_findByOwnerId() throws Exception {
        when(itemServise.findAllByOwnerId(1L, PageParam.createPageable(0, 5)))
                .thenReturn(List.of(dtoMaper.toDtoWithBooking(ITEM_ID1_OWNER1_AVALIBLE_TRUE,
                        List.of(commentDtoMaper.toDto(COMMENTID1_USER2)))));
        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
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
        when(itemServise.addComment(1L, 2L, COMMENTID1_USER2.getText()))
                .thenReturn(COMMENTID1_USER2);
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

    @Test
    void test5_findByText() throws Exception {
        when(itemServise.findByText("text", PageParam.createPageable(0, 5)))
                .thenReturn(List.of(ITEM_ID1_OWNER1_AVALIBLE_TRUE));
        mvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "5")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
    }
}