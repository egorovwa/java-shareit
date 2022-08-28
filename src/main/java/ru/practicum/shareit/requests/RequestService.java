package ru.practicum.shareit.requests;

import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.RequestNotExistException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.util.PageParam;

import java.util.Collection;


public interface RequestService {
    ItemRequest createRequest(Long userId, ItemRequestDto itemRequestDto) throws ModelNotExitsException;


    Collection<ItemRequest> findAllWithPage(PageParam pageParam, Long userId) throws ModelNotExitsException;

    ItemRequest findById(Long requestId) throws RequestNotExistException;

    ItemRequest save(ItemRequest itemRequest);

    Collection<ItemRequest> findAllForRequestor(Long userId) throws ModelNotExitsException;

    ItemRequest findItemRequest(Long itemId, Long userId) throws ModelNotExitsException;
}
