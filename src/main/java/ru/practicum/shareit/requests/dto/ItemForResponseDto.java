package ru.practicum.shareit.requests.dto;

import lombok.Data;

@Data
public class ItemForResponseDto {
    Long itemId;
    String ItemName;
    Long ownerId;
}
