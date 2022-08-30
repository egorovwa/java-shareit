package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDtoForRequestor {
    private Long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
    private Collection<ItemDto> items = new ArrayList<>();
}
