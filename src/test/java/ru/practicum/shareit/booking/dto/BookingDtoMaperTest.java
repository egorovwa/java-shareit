package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.TestConstants.*;

class BookingDtoMaperTest {
    final BookingDtoMaper bookingDtoMaper = new BookingDtoMaper();

    @Test
    void toDtoCreated() {
        Booking booking = new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC), ITEM_ID1_OWNER1_AVALIBLE_TRUE,
                USER_ID2, BookingStatus.WAITING);
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
        Booking booking = new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC), ITEM_ID1_OWNER1_AVALIBLE_TRUE,
                USER_ID2, BookingStatus.WAITING);
        BookingDto bookingDto =
                new BookingDto(1L, LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                        LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC), booking.getItem(),
                        USER_ID2, BookingStatus.WAITING);
        assertThat(bookingDtoMaper.toDto(booking), is(bookingDto));
    }
}