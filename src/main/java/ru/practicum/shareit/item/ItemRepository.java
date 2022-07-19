package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

   Optional<Item> findById(long itemId);

    Collection<Item> findByOwnerId(long userId);

    Collection<Item> findByText(String text);
}
