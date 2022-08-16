package ru.practicum.shareit.booking.exceptions;

import ru.practicum.shareit.exceptions.IncorrectUserIdException;

public class WrongUserIdException extends IncorrectUserIdException {
    public WrongUserIdException(String message, String userId) {
        super(message, userId);
    }
}
