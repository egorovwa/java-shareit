package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoToItem;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class ItemDtoWithBoking extends ItemDto {
    BookingDtoToItem lastBooking;
    BookingDtoToItem nextBooking;

    Collection<CommentDto> comments;

    public ItemDtoWithBoking(Long id, @NotBlank String name, @NotBlank String description, @NotNull Boolean available,
                             User owner, BookingDtoToItem lastBooking, BookingDtoToItem nextBooking,
                             Collection<CommentDto> comments) {
        super(id, name, description, available, owner);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }

    public ItemDtoWithBoking(Long id, @NotBlank String name, @NotBlank String description, @NotNull Boolean available,
                             User owner, Collection<CommentDto> comments) {
        super(id, name, description, available, owner);
        this.comments = comments;
    }
}
