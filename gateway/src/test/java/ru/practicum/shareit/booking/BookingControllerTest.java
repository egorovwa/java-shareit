package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingControllerTest {
    @MockBean
    BookingClient client;
    @Autowired
    ObjectMapper objectMapper;
    MockMvc mvc;
    private static final String API_PREFIX = "/bookings";
    private static final String HEADER_USER_ID = "X-Sharer-User-Id";

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void test1_1getBookings() throws Exception {
        mvc.perform(get(API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER_USER_ID, 1)
                .param("state", "ALL")
                .param("from", "0")
                .param("size", "2"));
        verify(client, times(1)).getBookings(1L, BookingState.ALL, 0, 2);
    }

    @Test
    void test1_2getBookings_whenStateError() throws Exception {
        mvc.perform(get(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HEADER_USER_ID, 1)
                        .param("state", "aaa")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(result -> {
                    assertTrue(result.getResolvedException() instanceof
                            IllegalArgumentException);
                });
    }

    @Test
    void test1_2getBookings_whenFromNegative() throws Exception {
        assertThrows(NestedServletException.class, () -> {
            mvc.perform(get(API_PREFIX)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header(HEADER_USER_ID, 1)
                    .param("state", "ALL")
                    .param("from", "-1")
                    .param("size", "2"));
        });
    }

    @Test
    void test1_3getBookings_whenSizeZero() throws Exception {
        assertThrows(NestedServletException.class, () -> {
            mvc.perform(get(API_PREFIX)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header(HEADER_USER_ID, 1)
                    .param("state", "ALL")
                    .param("from", "0")
                    .param("size", "0"));
        });
    }

    @Test
    void test2_1bookItem() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().plus(Duration.ofSeconds(1)),
                LocalDateTime.now().plus(Duration.ofSeconds(3)));
        mvc.perform(post(API_PREFIX)
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsString(dto))
                .header(HEADER_USER_ID, 1));
        verify(client, times(1)).bookItem(1L, dto);
    }

    @Test
    void test2_2bookItem_bookingTimeEroor() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto(1L, LocalDateTime.now().plus(Duration.ofSeconds(2)),
                LocalDateTime.now().plus(Duration.ofSeconds(1)));
        mvc.perform(post(API_PREFIX)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(dto))
                        .header(HEADER_USER_ID, 1))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
                        IllegalArgumentException))
                .andExpect(status().is5xxServerError());

    }

    @Test
    void test_3_1getBooking() throws Exception {
        mvc.perform(get(API_PREFIX + "/{bookingId}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER_USER_ID, 1));
        verify(client, times(1)).getBookings(1L, 1L);

    }

    @Test
    void test4_1bookingСonfirmation() throws Exception {
        mvc.perform(patch(API_PREFIX + "/{bookingId}", 1)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_USER_ID, 1)
                .param("approved", "true"));
        verify(client, times(1)).patchBooking(1L, 1L, true);
    }

    @Test
    void test5_1getAllOwner() throws Exception {
        mvc.perform(get(API_PREFIX + "/owner")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .header(HEADER_USER_ID, 1)
                .param("state", "all")
                .param("forom", "0")
                .param("size", "2"));
        verify(client, times(1)).getBookings("/owner", 1L,
                BookingState.ALL, 0, 2);
    }

    @Test
    void test5_2getAllOwner_whenStateError() throws Exception {
        mvc.perform(get(API_PREFIX + "/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header(HEADER_USER_ID, 1)
                        .param("state", "ffff")
                        .param("forom", "0")
                        .param("size", "2"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
                        IllegalArgumentException));
    }

    @Test
    void test5_3getAllOwner_whenFromNegatie() throws Exception {
        assertThrows(NestedServletException.class, () -> {
            mvc.perform(get(API_PREFIX + "/owner")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header(HEADER_USER_ID, 1)
                    .param("state", "ALL")
                    .param("from", "-1")
                    .param("size", "2"));
        });
    }
    @Test
    void test5_4getAllOwner_whenSize0() throws Exception {
        assertThrows(NestedServletException.class, () -> {
            mvc.perform(get(API_PREFIX + "/owner")
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding(StandardCharsets.UTF_8)
                    .header(HEADER_USER_ID, 1)
                    .param("state", "ALL")
                    .param("from", "0")
                    .param("size", "0"));
        });
    }
}