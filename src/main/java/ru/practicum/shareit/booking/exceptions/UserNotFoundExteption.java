package ru.practicum.shareit.booking.exceptions;

import ru.practicum.shareit.exceptions.ModelNotExitsException;

public class UserNotFoundExteption extends ModelNotExitsException {
    public UserNotFoundExteption(String message, String param, String value) {
        super(message, param, value);
    }
}
