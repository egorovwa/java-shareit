package ru.practicum.shareit.exceptions;

public class IncorectUserOrItemIdException extends Exception{
    final Long userId;
    final Long itemId;

    public IncorectUserOrItemIdException(String message, Long userId, Long itemId) {
        super(message);
        this.userId = userId;
        this.itemId = itemId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getItemId() {
        return itemId;
    }
}
