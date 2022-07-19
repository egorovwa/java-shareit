package ru.practicum.shareit.exceptions;

public class UserAlreadyExistsException extends UserCrudException{
    public UserAlreadyExistsException(String message, String param, String value) {
        super(message, param, value);
    }
}
