package ru.practicum.shareit.requests;

import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.RequestNotExistException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.Collection;


public interface RequestService {
ItemRequest createRequest(Long userId, ItemRequestDto itemRequestDto) throws ModelNotExitsException;


    Collection<ItemRequest> findAllWithPage(Integer from, Integer size, Long userId) throws ModelNotExitsException; // TODO: 17.08.2022 aplication test

    ItemRequest findById(Long requestId) throws RequestNotExistException;

    void save(ItemRequest itemRequest);
    Collection<ItemRequest> findAllForRequestor(Long userId) throws ModelNotExitsException;
    ItemRequest findItemRequest(Long itemId, Long userId) throws ModelNotExitsException;
}
