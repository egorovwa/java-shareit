package ru.practicum.shareit.exceptions;

public class ModelNotExitsException extends ModelCrudException {
    public ModelNotExitsException(String message, String param, String value) {
        super(message, param, value);
    }
}
