package ru.practicum.shareit.requests.dto;

import ru.practicum.shareit.requests.ItemRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class ItemRequestDtoMaper {
    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequestor(),
                LocalDateTime.ofInstant(Instant.ofEpochSecond(itemRequest.getCreated()), ZoneOffset.systemDefault()));
    }

    public ItemRequest fromDto(ItemRequestDto dto) {
        return new ItemRequest(dto.getId(), dto.getDescription(), dto.getRequestor(),
                dto.getCreated().atZone(ZoneId.systemDefault()).toEpochSecond());
    }
}
