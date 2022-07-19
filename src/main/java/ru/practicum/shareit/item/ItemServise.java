package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemServise {
    ItemDto createItem(long userId, ItemDto itemDto) throws ModelNotExitsException, IncorrectUserIdException;

    ItemDto patchItem(long userId, long itemId, ItemDto itemDto) throws ModelNotExitsException, IncorectUserOrItemIdException;

    ItemDto findByIdDto(long itemId) throws ModelNotExitsException;
    Item findById(long itemId) throws ModelNotExitsException;

    Collection<ItemDto> findAllByOwnerId(long userId);

    Collection<ItemDto> findByText(String text);
}
