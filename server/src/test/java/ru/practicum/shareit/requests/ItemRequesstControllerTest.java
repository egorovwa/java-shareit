package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoForRequestor;
import ru.practicum.shareit.requests.dto.ItemRequestDtoMaper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.TestConstants.*;

@WebMvcTest(controllers = ItemRequesstController.class)
@Import({ItemRequestDtoMaper.class, ItemDtoMaper.class, BookingDtoMaper.class})
class ItemRequesstControllerTest {
    public static final String USER_ID_HEADER_NAME = "X-Sharer-User-Id";
    final LocalDateTime createTime = LocalDateTime.of(2022, 8, 16, 19, 9, 0);
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @MockBean
    RequestService requestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    User user;

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
                createTime.toEpochSecond(ZoneOffset.UTC), null);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

        when(requestService.createRequest(anyLong(), any()))
                .thenReturn(itemRequest);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER_NAME, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequest.getId()))
                .andExpect(jsonPath("$.requestor.email", is(user.getEmail())))
                .andExpect(jsonPath("$.created", is(timeFormatter.format(createTime))))
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

    @Test
    void test2_findAllWithPage() throws Exception {

        List<ItemRequest> itemRequests = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            itemRequests.add(new ItemRequest(i, "aaaaaa",
                    USER_ID1, TEST_TIME_LONG, new ArrayList<>()));
        }
        when(requestService.findAllWithPage(Mockito.any(PageRequest.class), anyLong()))
                .thenReturn(itemRequests);


        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER_NAME, 1L)
                        .param("from", "0")
                        .param("size", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)));
    }

    @Test
    void test5_findAllForRequestor() throws Exception {
        when(requestService.findAllForRequestor(1L))
                .thenReturn(List.of(ITEMREQUEST_ID1_USER1));
        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));

    }

    @Test
    void test3_findtemRequest() throws Exception {
        when(requestService.findItemRequest(1L, 1L))
                .thenReturn(new ItemRequest(1L, "aaaaaa",
                        USER_ID1, TEST_TIME_LONG, new ArrayList<>()));
        ItemRequestDtoForRequestor dto = new ItemRequestDtoForRequestor(1L, "aaaaaa",
                USER_ID1, TEST_TIME_DATE_TIME, new ArrayList<>());
        mvc.perform(get("/requests/{itemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER_NAME, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.requestor.email", is(USER_ID1.getEmail())));

    }

    @Test
    void test4_createRequestWithWrongUserId() throws Exception {
        ItemRequest itemRequest = new ItemRequest(1L, "description", user,
                createTime.toEpochSecond(ZoneOffset.UTC), null);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("description");

        when(requestService.createRequest(anyLong(), any()))
                .thenThrow(new ModelNotExitsException("message", "id", "1"));
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(USER_ID_HEADER_NAME, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
                        ModelNotExitsException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage(), is("message")));
    }


}