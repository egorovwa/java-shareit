package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToItem;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
public class ItemDtoWithBoking extends ItemDto{
    BookingDtoToItem lastBooking;
    BookingDtoToItem nextBooking;

    public ItemDtoWithBoking(Long id, @NotBlank String name, @NotBlank String description, @NotNull Boolean available,
                             User owner, BookingDtoToItem lastBooking, BookingDtoToItem nextBooking) {
        super(id, name, description, available, owner);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }

    public ItemDtoWithBoking(BookingDtoToItem lastBooking, BookingDtoToItem nextBooking) {
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }

    public ItemDtoWithBoking(Long id, String name, String description, Boolean available, User owner) {
        super(id, name, description, available, owner);
    }
}
