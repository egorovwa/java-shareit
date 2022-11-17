package ru.practicum.shareit.exceptions;

public class RequestNotExistException extends ModelNotExitsException {
    public RequestNotExistException(String message, String param, String value) {
        super(message, param, value);
    }
}
