package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.exceptions.UnknownStateException;

public class StateDtoMaper {
    public static BookingState fromDto(String dtoStr) throws UnknownStateException {
        switch (dtoStr.toUpperCase()) {
            case "ALL":
                return BookingState.ALL;
            case "CURRENT":
                return BookingState.CURRENT;
            case "PAST":
                return BookingState.PAST;
            case "FUTURE":
                return BookingState.FUTURE;
            case "WAITING":
                return BookingState.WAITING;
            case "REJECTED":
                return BookingState.REJECTED;
            default:
                throw new UnknownStateException("Не известный state", dtoStr);
        }


    }
}
