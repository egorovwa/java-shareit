package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiseImpl implements ItemServise {
    private final UserServise userServise;
    private final ItemRepository itemRepository;

    @Override
    public Item createItem(long userId, Item item) throws IncorrectUserIdException {
        try {
            User user = userServise.findById(userId);
            item.setOwner(user);
            log.info("Добавленна item ({}), пользователем id {}", item.getName(), userId);
            return itemRepository.save(item);
        } catch (ModelNotExitsException e) {
            log.warn("Попытка добавление item ({}), несуществующим пользователем id {}", item.getName(), userId);
            throw new IncorrectUserIdException("Пользователь не найден", String.valueOf(userId));
        }
    }

    @Override
    public Item patchItem(long userId, long itemId, Item item) throws ModelNotExitsException,
            IncorectUserOrItemIdException {
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("Вещь не найденна", "id", String.valueOf(itemId)));
        if (updatedItem.getOwner().equals(userServise.findById(userId))) {
            if (item.getName() != null) {
                log.info("Обновление имени вещи id {} newName = {}", updatedItem.getId(), item.getName());
                updatedItem.setName(item.getName());
            }
            if (item.getDescription() != null) {
                log.info("Обновление описания вещи id {} newDescription = {}", updatedItem.getId(), item.getDescription());
                updatedItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                log.info("Обновление Available вещи id {} newAvailable = {}", updatedItem.getId(), item.getAvailable());
                updatedItem.setAvailable(item.getAvailable());
            }
            return itemRepository.save(updatedItem);
        } else {
            log.warn("Пользователь id {} пытается изменить вещь id {}", userId, itemId);
            throw new IncorectUserOrItemIdException("Вещь не принадлежит пользователю", userId, itemId);
        }

    }

    @Override
    public Item findById(long itemId) throws ModelNotExitsException {
        log.info("поиск вещи id ={}", itemId);
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("Вещь не найденна", "id", String.valueOf(itemId)));
    }

    @Override
    public Collection<Item> findAllByOwnerId(long userId) {
        log.info("поиск вещей пользователя id ={}", userId);
        return Collections.unmodifiableCollection(itemRepository.findByOwnerId(userId));
    }

    @Override
    public Collection<Item> findByText(String text) {
        if (Strings.isNotBlank(text)) {
            log.info("поиск вещей по тексту ({})", text);
            return Collections.unmodifiableCollection(itemRepository.findByText(text));
        } else return new ArrayList<>();
    }
}
