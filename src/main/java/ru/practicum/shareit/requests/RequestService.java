package ru.practicum.shareit.requests;

import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;


public interface RequestService {
ItemRequest createRequest(Long userId, ItemRequestDto itemRequestDto) throws ModelNotExitsException;

}
