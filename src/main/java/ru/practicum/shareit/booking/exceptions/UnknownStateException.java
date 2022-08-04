package ru.practicum.shareit.booking.exceptions;

public class UnknownStateException extends Exception{
    String value;

    public UnknownStateException(String message, String value) {
        super(message);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
