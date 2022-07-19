package ru.practicum.shareit.exceptions;

public class UserCrudException extends Exception {      // TODO: 18.07.2022 название придумать
    String param;
    String value;

    public UserCrudException(String message, String param, String value) {
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
