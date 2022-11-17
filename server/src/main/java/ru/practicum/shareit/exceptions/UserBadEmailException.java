package ru.practicum.shareit.exceptions;

import javax.validation.ValidationException;

public class UserBadEmailException extends ValidationException {
    public UserBadEmailException(String message) {
        super(message);
    }
}
