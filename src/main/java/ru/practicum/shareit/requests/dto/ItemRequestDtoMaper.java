package ru.practicum.shareit.requests.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestDtoMaper {
    private final ItemDtoMaper itemDtoMaper;
    public ItemRequestDto toCreatedDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getRequestor(),LocalDateTime.ofEpochSecond(itemRequest.getCreated(),0, ZoneOffset.UTC));
    }

    public ItemRequest fromDto(ItemRequestDto dto) {
        return new ItemRequest(dto.getDescription(), dto.getRequestor());
    }
    public ItemRequestDtoForRequestor toDtoForRequestor(ItemRequest itemRequest){
        return new ItemRequestDtoForRequestor(itemRequest.getId(),itemRequest.getDescription(),
                itemRequest.getRequestor(),
                LocalDateTime.ofEpochSecond(itemRequest.getCreated(),0,ZoneOffset.UTC),
                itemRequest.getItems().stream().map(itemDtoMaper::toDto).collect(Collectors.toList()));
    }
}
