package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
@NoArgsConstructor
public class ItemDtoWithBoking extends ItemDto{
    Booking lastBooking;
    Booking nextBooking;

    public ItemDtoWithBoking(Long id, @NotBlank String name, @NotBlank String description, @NotNull Boolean available,
                             User owner, Booking lastBooking, Booking nextBooking) {
        super(id, name, description, available, owner);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }

    public ItemDtoWithBoking(Booking lastBooking, Booking nextBooking) {
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }

    public ItemDtoWithBoking(Long id, String name, String description, Boolean available, User owner) {
        super(id, name, description, available, owner);
    }
}
