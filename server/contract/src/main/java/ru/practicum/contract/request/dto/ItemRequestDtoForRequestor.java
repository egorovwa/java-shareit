package ru.practicum.contract.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.contract.item.dto.ItemDtoCreated;
import ru.practicum.contract.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDtoForRequestor {
    private Long id;
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    private Collection<ItemDtoCreated> items = new ArrayList<>();
}
