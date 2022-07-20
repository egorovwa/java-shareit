package ru.practicum.shareit.exceptions;

public class ModelAlreadyExistsException extends ModelCrudException {
    public ModelAlreadyExistsException(String message, String param, String value) {
        super(message, param, value);
    }
}
