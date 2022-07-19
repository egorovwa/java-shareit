package ru.practicum.shareit.exceptions;

public class UserNotExitsException extends UserCrudException{
    public UserNotExitsException(String message, String param, String value) {
        super(message, param, value);
    }
}
