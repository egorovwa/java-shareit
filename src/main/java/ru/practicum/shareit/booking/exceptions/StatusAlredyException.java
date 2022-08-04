package ru.practicum.shareit.booking.exceptions;

public class StatusAlredyException extends ItemNotAvalibleExxeption{
    public StatusAlredyException(String message, String id) {
        super(message, id);
    }
}
