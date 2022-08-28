package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.exceptions.UnknownStateException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.dto.StateDtoMaper.fromDto;


class StateDtoMaperTest {


    @Test
    void fromDto_ALL() throws UnknownStateException {
        assertThat(fromDto("All"), is(BookingState.ALL));
    }

    @Test
    void fromDto_FUTURE() throws UnknownStateException {
        assertThat(fromDto("FUTURE"), is(BookingState.FUTURE));
    }

    @Test
    void fromDto_PAST() throws UnknownStateException {
        assertThat(fromDto("PAST"), is(BookingState.PAST));
    }

    @Test
    void fromDto_CURRENT() throws UnknownStateException {
        assertThat(fromDto("CURRENT"), is(BookingState.CURRENT));
    }

    @Test
    void fromDto_WAITING() throws UnknownStateException {
        assertThat(fromDto("WAITING"), is(BookingState.WAITING));
    }

    @Test
    void fromDto_REJECTED() throws UnknownStateException {
        assertThat(fromDto("REJECTED"), is(BookingState.REJECTED));
    }

    @Test
    void fromDto_incorrect() {
        assertThrows(UnknownStateException.class, () -> fromDto("sdad"));
    }

}