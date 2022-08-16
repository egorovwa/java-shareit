package ru.practicum.shareit.booking.exceptions;

public class ItemNotAvalibleExxeption extends Exception {
    final String id;

    public ItemNotAvalibleExxeption(String message, String id) {
        super(message);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
