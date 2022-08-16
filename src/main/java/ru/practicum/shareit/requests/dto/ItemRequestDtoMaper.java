package ru.practicum.shareit.requests.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class ItemRequestDtoMaper {
    public ItemRequestDto toCreatedDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequestor(),LocalDateTime.ofEpochSecond(itemRequest.getCreated(),0, ZoneOffset.UTC));
    }

    public ItemRequest fromDto(ItemRequestDto dto) {
        return new ItemRequest(dto.getDescription(), dto.getRequestor());
    }
}
