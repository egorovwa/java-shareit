package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemServise {
    Item createItem(long userId, Item item) throws IncorrectUserIdException;

    Item patchItem(long userId, long itemId, Item item) throws ModelNotExitsException, IncorectUserOrItemIdException;

    Item findById(long itemId) throws ModelNotExitsException;

    Collection<Item> findAllByOwnerId(long userId);

    Collection<Item> findByText(String text);
}
