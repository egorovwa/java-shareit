package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Entitys.BOOKING1_USER2_ITEM1_WAITING;

@WebMvcTest(BookingController.class)
@Import(BookingDtoMaper.class)
class BookingControllerTest {
    @MockBean
    BookingServise bookingServise;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;
    BookingDtoToCreate dtoToCreate;
    Booking booking;
    BookingDtoMaper dtoMaper = new BookingDtoMaper();
    final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
        booking = BOOKING1_USER2_ITEM1_WAITING;
        dtoToCreate = new BookingDtoToCreate(
                LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC),
                1L);

    }

    @Test
    void test1_1_createBooking_ModelNotFound() throws Exception {
        when(bookingServise.createBooking(Mockito.any(), Mockito.anyLong()))
                .thenThrow(new ModelNotExitsException("message", "param", "value"));
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(dtoToCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(ModelNotExitsException.class)));
    }

    @Test
    void test1_2_createBooking_TimeIntersection() throws Exception {
        when(bookingServise.createBooking(Mockito.any(), Mockito.anyLong()))
                .thenThrow(new TimeIntersectionException("message", LocalDateTime.now(), LocalDateTime.now()));
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(dtoToCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(TimeIntersectionException.class)));
    }

    @Test
    void test1_3_createBooking_ItemNotAvalible() throws Exception {
        when(bookingServise.createBooking(Mockito.any(), Mockito.anyLong()))
                .thenThrow(new ItemNotAvalibleExxeption("message", "id"));
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(dtoToCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(ItemNotAvalibleExxeption.class)));
    }

    @Test
    void test1_4_createBooking() throws Exception {
        when(bookingServise.createBooking(Mockito.any(), Mockito.anyLong()))
                .thenReturn(BOOKING1_USER2_ITEM1_WAITING);
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(dtoToCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }


    @Test
    void test2_1bookingСonfirmation_incorrectUserId() throws Exception {
        when(bookingServise.setStatus(1L, 1L, true))
                .thenThrow(new IncorrectUserIdException("message", "id"));
        mvc.perform(patch("/bookings//{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(IncorrectUserIdException.class)));
    }

    @Test
    void test2_2_bookingСonfirmation_ParametrNotFound() throws Exception {
        when(bookingServise.setStatus(1L, 1L, true))
                .thenThrow(new ParametrNotFoundException("message", "param"));
        mvc.perform(patch("/bookings//{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(ParametrNotFoundException.class)));
    }

    @Test
    void test2_3_bookingСonfirmation_StatusAlredyException() throws Exception {
        when(bookingServise.setStatus(1L, 1L, true))
                .thenThrow(new StatusAlredyException("message", "id"));
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(StatusAlredyException.class)));
    }

    @Test
    void test2_4_bookingСonfirmation() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingServise.setStatus(1L, 1L, true))
                .thenReturn(booking);
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", String.valueOf(true))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }

    @Test
    void test5_getById() throws Exception {
        when(bookingServise.findById(1L, 2L))
                .thenReturn(BOOKING1_USER2_ITEM1_WAITING);
        mvc.perform(get("/bookings/{bookingId}", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start",
                        is(timeFormatter.format(LocalDateTime.ofEpochSecond(BOOKING1_USER2_ITEM1_WAITING.getStart(),
                                0, ZoneOffset.UTC)))));
    }

    @Test
    void test3_1_getAll_userNotFound() throws Exception {
        when(bookingServise.getAllUser(1L, BookingState.ALL, 0, 3))
                .thenThrow(new UnknownStateException("message", "value"));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "dfd")
                        .param("from", "0")
                        .param("size", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is5xxServerError())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(UnknownStateException.class)));

    }

    @Test
    void test4_1getAllUser_withOutState() throws Exception {
        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .param("from", "0")
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));
        Mockito
                .verify(bookingServise, Mockito.times(1)).getAllUser(1L, 0, 3);
    }

    @Test
    void test4_1getAllUser_withState() throws Exception {
        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", 1L)
                .param("state", String.valueOf(BookingState.FUTURE))
                .param("from", "0")
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));
        Mockito
                .verify(bookingServise, Mockito.times(1))
                .getAllUser(1L, BookingState.FUTURE, 0, 3);
    }

    @Test
    void test4_1getAllOwner_withOutState() throws Exception {
        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1L)
                .param("from", "0")
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));
        Mockito
                .verify(bookingServise, Mockito.times(1))
                .getAllOwner(1L, 0, 3);
    }

    @Test
    void test1_1getAllOwner_withState() throws Exception {
        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", 1L)
                .param("state", String.valueOf(BookingState.FUTURE))
                .param("from", "0")
                .param("size", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));
        Mockito
                .verify(bookingServise, Mockito.times(1))
                .getAllOwner(1L, BookingState.FUTURE);
    }
}