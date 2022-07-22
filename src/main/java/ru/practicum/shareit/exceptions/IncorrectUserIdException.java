package ru.practicum.shareit.exceptions;

public class IncorrectUserIdException extends Exception {
    final String userId;

    public IncorrectUserIdException(String message, String userId) {
        super(message);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
