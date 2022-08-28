package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.Entitys.BOOKING1_USER2_ITEM1_WAITING;
import static ru.practicum.shareit.Entitys.USER_ID2;

class BookingDtoMaperTest {
    final BookingDtoMaper bookingDtoMaper = new BookingDtoMaper();

    @Test
    void toDtoCreated() {
        Booking booking = BOOKING1_USER2_ITEM1_WAITING;
        BookingDto bookingDto =
                new BookingDto(1L, LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                        LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC), booking.getItem(),
                        USER_ID2, BookingStatus.WAITING);
        assertThat(bookingDtoMaper.toDtoCreated(booking), is(bookingDto));

    }

    @Test
    void toItemDto() {
        Booking booking = BOOKING1_USER2_ITEM1_WAITING;
        BookingDtoToItem dto =
                new BookingDtoToItem(1L, LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                        LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC),
                        booking.getItem().getId(), USER_ID2.getId(), BookingStatus.WAITING);
        assertThat(bookingDtoMaper.toItemDto(Optional.of(booking)), is(dto));
    }

    @Test
    void toDto() {
        Booking booking = BOOKING1_USER2_ITEM1_WAITING;
        BookingDto bookingDto =
                new BookingDto(1L, LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                        LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC), booking.getItem(),
                        USER_ID2, BookingStatus.WAITING);
        assertThat(bookingDtoMaper.toDto(booking), is(bookingDto));
    }
}