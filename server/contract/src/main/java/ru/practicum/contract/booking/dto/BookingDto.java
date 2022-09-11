package ru.practicum.contract.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.contract.item.dto.ItemDto;
import ru.practicum.contract.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
}
