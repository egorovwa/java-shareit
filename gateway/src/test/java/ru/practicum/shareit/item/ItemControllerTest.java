package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String API_PREFIX = "/items";
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";
    @MockBean
    ItemClient client;
    @Autowired
    ObjectMapper objectMapper;
    MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void test1_1_createItem() throws Exception {
        ItemDto dto = new ItemDto(null, "name", "dddddd", true, 1L);
        mvc.perform(post(API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(dto))
                .header(HEADER_USER_ID, 1L));
        verify(client, times(1)).postItem(1L, dto);
    }

    @Test
    void test1_2_createItem_nameBlank() throws Exception {
        ItemDto dto = new ItemDto(null, "", "dddddd", true, 1L);
        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HEADER_USER_ID, 1L))
                .andExpect(status().isBadRequest());

    }

    @Test
    void test1_3_createItem_descriptionBlank() throws Exception {
        ItemDto dto = new ItemDto(null, "name", "", true, 1L);

        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HEADER_USER_ID, 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test2_1patchItem() throws Exception {
        ItemDto dto = new ItemDto(null, "update", null, null, null);
        mvc.perform(patch(API_PREFIX + "/{itemId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(dto))
                .header(HEADER_USER_ID, 1L));
        verify(client, times(1)).pacthItem(1L, 1L, dto);
    }

    @Test
    void test2_2patchItem_when_itemNotFound() throws Exception {
        ItemDto dto = new ItemDto(null, "update", null, null, null);
        when(client.pacthItem(1L, 1L, dto))
                .thenReturn(ResponseEntity.notFound().build());
        mvc.perform(patch(API_PREFIX + "/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HEADER_USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void test2_3patchItem_whenNameBlank() throws Exception {
        ItemDto dto = new ItemDto(null, "", null, null, null);
        when(client.pacthItem(1L, 1L, dto))
                .thenReturn(ResponseEntity.notFound().build());
        mvc.perform(patch(API_PREFIX + "/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HEADER_USER_ID, 1L))
                .andExpect(status().isBadRequest());

    }


    @Test
    void test3_1findById() throws Exception {
        mvc.perform(get(API_PREFIX + "/{itemId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER_USER_ID, 1));
        verify(client, times(1)).getItem(1L, 1L);
    }

    @Test
    void test3_2findById_itemNotFoud() throws Exception {
        when(client.getItem(1L, 1L))
                .thenReturn(ResponseEntity.notFound().build());
        mvc.perform(get(API_PREFIX + "/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HEADER_USER_ID, 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void test4_1findAllByOwnerId() throws Exception {
        mvc.perform(get(API_PREFIX)
                .header(HEADER_USER_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .param("from", "1")
                .param("size", "1"));
        verify(client, times(1)).getItems(1L, 1, 1);
    }

    @Test
    void test4_2findAllByOwnerId_whenfromNegative() throws Exception {
        mvc.perform(get(API_PREFIX)
                        .header(HEADER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "-1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void test4_2findAllByOwnerId_whenSizeZero() throws Exception {
        mvc.perform(get(API_PREFIX)
                        .header(HEADER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test5_1findByText() throws Exception {
        mvc.perform(get(API_PREFIX + "/search")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_USER_ID, 1)
                .param("text", "text")
                .param("from", "0")
                .param("size", "2"));
        verify(client, times(1)).getItems("/search", "text", 2, 0, 1L);

    }

    @Test
    void test5_3findByText_whenFromNegative() throws Exception {
        mvc.perform(get(API_PREFIX + "/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1)
                        .param("text", "text")
                        .param("from", "-1")
                        .param("size", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test5_4findByText_whenSizeZero() throws Exception {
        mvc.perform(get(API_PREFIX + "/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER_USER_ID, 1)
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test6_1addComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("text");
        mvc.perform(post(API_PREFIX + "/{itemId}/comment", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(commentDto))
                .header(HEADER_USER_ID, 1L));
        verify(client, times(1))
                .postComment("/1/comment", 1L, commentDto);
    }

    @Test
    void test6_2addComment_whenTextsizeError() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("t");

        mvc.perform(post(API_PREFIX + "/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .header(HEADER_USER_ID, 1L))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
                        MethodArgumentNotValidException));


    }
}