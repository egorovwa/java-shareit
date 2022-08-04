package ru.practicum.shareit.booking.exceptions;

public class ParametrNotFoundException extends Exception{
    String param;

    public ParametrNotFoundException(String message, String param) {
        super(message);
        this.param = param;
    }

    public String getParam() {
        return param;
    }
}
