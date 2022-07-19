package ru.practicum.shareit.item.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class InMemoryItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private long id = 1;

    @Override
    public Item save(Item item) {
        if (item.getId() != null) {
            itemMap.put(item.getId(), item);
        } else {
            item.setId(id);
            id++;
            itemMap.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Optional<Item> findById(long itemId) {

        return itemMap.keySet().stream().filter(r -> r == itemId).findAny().map(itemMap::get);
    }

    @Override
    public Collection<Item> findByOwnerId(long userId) {
        return itemMap.values().stream()
                .filter(it -> it.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> findByText(String text) {
        return itemMap.values().stream().filter(it -> it.getName().toLowerCase().contains(text.toLowerCase())
                || it.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
