package ru.practicum.shareit.exceptions;

public class ModelCrudException extends Exception {
    final String param;
    final String value;

    public ModelCrudException(String message, String param, String value) {
        super(message);
        this.param = param;
        this.value = value;
    }

    public String getParam() {
        return param;
    }

    public String getValue() {
        return value;
    }
}
