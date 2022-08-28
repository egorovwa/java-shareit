package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBoking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemServise {
    Item createItem(long userId, ItemDto itemDto) throws IncorrectUserIdException;

    Item patchItem(long userId, long itemId, Item item) throws ModelNotExitsException, IncorectUserOrItemIdException;

    ItemDtoWithBoking findById(long itemId, long userId) throws ModelNotExitsException;

    Item findById(long itemId) throws ModelNotExitsException;

    Collection<ItemDtoWithBoking> findAllByOwnerId(long userId, Integer from, Integer size);

    Collection<Item> findByText(String text, Integer from, Integer size);

    Comment addComment(Long itemId, long userId, String text) throws ModelNotExitsException, NotUsedCommentException;
}
