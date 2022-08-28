package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.NotUsedCommentException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBoking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.PageParam;

import java.util.Collection;

public interface ItemServise {
    Item createItem(long userId, ItemDto itemDto) throws IncorrectUserIdException;

    Item patchItem(long userId, long itemId, Item item) throws ModelNotExitsException, IncorectUserOrItemIdException;

    ItemDtoWithBoking findById(long itemId, long userId) throws ModelNotExitsException;

    Item findById(long itemId) throws ModelNotExitsException;

    Collection<ItemDtoWithBoking> findAllByOwnerId(long userId, PageParam pageParam);

    Collection<Item> findByText(String text, PageParam pageParam);

    Comment addComment(Long itemId, long userId, String text) throws ModelNotExitsException, NotUsedCommentException;
}
