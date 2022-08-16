package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoMaper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.text.Format;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
@Import(ItemRequestDtoMaper.class)
class RequestControllerTest {
    @MockBean
    RequestService requestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    User user;
    LocalDateTime createTime = LocalDateTime.of(2022, 8, 16, 19, 9, 0);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
        user = new User(1L,
                "Email@mail.con",
                "user");
    }

    @Test
    void test1_createItemRequest() throws Exception {
        ItemRequest itemRequest = new ItemRequest(1L, "description", user,
                createTime.toEpochSecond(ZoneOffset.UTC));
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

        when(requestService.createRequest(anyLong(), any()))
                .thenReturn(itemRequest);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.requestor.email", is(user.getEmail())))
                .andExpect(jsonPath("$.created",is(timeFormatter.format(createTime))))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));

        itemRequestDto.setDescription("");
        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(itemRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header("X-Sharer-User-Id", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }



}