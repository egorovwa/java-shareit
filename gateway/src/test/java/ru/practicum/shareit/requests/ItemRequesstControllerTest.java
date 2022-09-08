package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.ConstraintViolationException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequesstController.class)
class ItemRequesstControllerTest {
    @MockBean
    ItemRequestsClient client;
    @Autowired
    ObjectMapper objectMapper;
    MockMvc mvc;
    private static final String API_PREFIX = "/requests";
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void test1_1_createRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(null, "ddddd", null);
        mvc.perform(post(API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1)
                .content(objectMapper.writeValueAsString(dto)));
        verify(client, times(1)).postRequest(1L, dto);
    }

    @Test
    void test1_2_createRequest_whenDescriptionBlank() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(null, "", null);
        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test1_3_createRequest_whenUserNotFound() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(null, "dfgdfgfd", null);
        when(client.postRequest(1L, dto))
                .thenReturn(ResponseEntity.badRequest().build());
        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test_2_1findAllItemRequest() throws Exception {
        when(client.getRequests("/all", 1L, 0, 3))
                .thenReturn(ResponseEntity.accepted().build());
        mvc.perform(get(API_PREFIX + "/all")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER_USER_ID, 1)
                .param("from", "0")
                .param("size", "3"));
        verify(client, times(1)).getRequests("/all", 1L, 0, 3);
    }

    @Test
    void test_2_2findAllItemRequest_whenFromNegative() throws Exception {
        when(client.getRequests("/all", 1L, 0, 3))
                .thenReturn(ResponseEntity.accepted().build());
            mvc.perform(get(API_PREFIX + "/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header(HEADER_USER_ID, 1)
                    .param("from", "-1")
                    .param("size", "3"))
                    .andExpect(status().isBadRequest());
    }
    @Test
    void test_2_3findAllItemRequest_whenSizeZero() throws Exception {
        when(client.getRequests("/all", 1L, 0, 3))
                .thenReturn(ResponseEntity.accepted().build());
            mvc.perform(get(API_PREFIX + "/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header(HEADER_USER_ID, 1)
                    .param("from", "0")
                    .param("size", "0"))
                    .andExpect(status().isBadRequest());

    }
    @Test
    void test_2_4findAllItemRequest_whenUserNotFound() throws Exception {
        when(client.getRequests("/all", 1L, 0, 3))
                .thenReturn(ResponseEntity.badRequest().build());
        mvc.perform(get(API_PREFIX + "/all")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER_USER_ID, 1)
                .param("from", "0")
                .param("size", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test3_1findAllForRequestor() throws Exception {
        mvc.perform(get(API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER_USER_ID, 1));
        verify(client, times(1)).getRequests(1L);
    }
    @Test
    void test3_2findAllForRequestor_userNotFound() throws Exception {
        when(client.getRequests(1L))
                .thenReturn(ResponseEntity.badRequest().build());
        mvc.perform(get(API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER_USER_ID, 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test4_1findItemRequest() throws Exception {
        mvc.perform(get(API_PREFIX+"/{itemId}", 1)
                .header(HEADER_USER_ID, 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        verify(client,times(1)).getRequests(1L,1L);
    }
    @Test
    void test4_1findItemRequest_whenResponseNotFound() throws Exception {
        when(client.getRequests(1L,1L))
                .thenReturn(ResponseEntity.notFound().build());
        mvc.perform(get(API_PREFIX+"/{itemId}", 1)
                .header(HEADER_USER_ID, 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}