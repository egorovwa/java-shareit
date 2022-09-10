package ru.practicum.shareit.requests.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.contract.request.dto.ItemRequestDto;
import ru.practicum.contract.request.dto.ItemRequestDtoForRequestor;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoMaper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestDtoMaper {
    private final ItemDtoMaper itemDtoMaper;
    private final UserDtoMaper userDtoMaper;

    public ItemRequestDto toCreatedDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                userDtoMaper.toDto(itemRequest.getRequestor()), LocalDateTime.ofEpochSecond(itemRequest.getCreated(),
                0, ZoneOffset.UTC));
    }
    public ItemRequestDtoForRequestor toDtoForRequestor(ItemRequest itemRequest) {
        return new ItemRequestDtoForRequestor(itemRequest.getId(), itemRequest.getDescription(),
                userDtoMaper.toDto(itemRequest.getRequestor()),
                LocalDateTime.ofEpochSecond(itemRequest.getCreated(), 0, ZoneOffset.UTC),
                itemRequest.getItems().stream().map(itemDtoMaper::toDtoCreated).collect(Collectors.toList()));
    }
}
