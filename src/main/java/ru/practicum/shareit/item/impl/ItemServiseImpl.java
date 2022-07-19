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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiseImpl implements ItemServise {
    private final UserServise userServise;
    private final ItemRepository itemRepository;
    private final ItemDtoMaper itemDtoMaper;

    @Override
    public ItemDto createItem(long userId, ItemDto itemDto) throws ModelNotExitsException, IncorrectUserIdException {
        try {
            User user = userServise.findById(userId);
            Item item = itemDtoMaper.fromDto(itemDto);
            item.setOwner(user);
            return itemDtoMaper.toDto(itemRepository.save(item));
        } catch (ModelNotExitsException e) {
            throw new IncorrectUserIdException("Пользователь не найден", String.valueOf(userId));
        }
    }

    @Override
    public ItemDto patchItem(long userId, long itemId, ItemDto itemDto) throws ModelNotExitsException,
            IncorectUserOrItemIdException {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("Вещь не найденна", "id", String.valueOf(itemId)));
        if (item.getOwner().equals(userServise.findById(userId))) {
            itemDtoMaper.update(item, itemDto);
            return itemDtoMaper.toDto(item);
        } else {
            log.warn("Пользователь id {} пытается изменить вещь id {}", userId, itemId);
            throw new IncorectUserOrItemIdException("Вещь не принадлежит пользователю", userId, itemId);
        }

    }

    @Override
    public ItemDto findByIdDto(long itemId) throws ModelNotExitsException {
        return itemDtoMaper.toDto(itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("Вещь не найденна", "id", String.valueOf(itemId))));
    }

    @Override
    public Item findById(long itemId) throws ModelNotExitsException {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("Вещь не найденна", "id", String.valueOf(itemId)));
    }

    @Override
    public Collection<ItemDto> findAllByOwnerId(long userId) {
        return itemRepository.findByOwnerId(userId).stream().map(itemDtoMaper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findByText(String text) {
        if (Strings.isNotBlank(text)) {
            return itemRepository.findByText(text).stream()
                    .map(itemDtoMaper::toDto)
                    .collect(Collectors.toList());
        }else return new ArrayList<>();
    }
}
