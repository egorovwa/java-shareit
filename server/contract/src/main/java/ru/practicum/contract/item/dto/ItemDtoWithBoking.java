package ru.practicum.contract.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.contract.booking.dto.BookingDtoToItem;
import ru.practicum.contract.user.dto.UserDto;

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
    UserDto owner;

    public ItemDtoWithBoking(Long id, @NotBlank String name, @NotBlank String description, @NotNull Boolean available,
                             UserDto owner, BookingDtoToItem lastBooking, BookingDtoToItem nextBooking,
                             Collection<CommentDto> comments) {
        super(id, name, description, available);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
        this.owner = owner;
    }

    public ItemDtoWithBoking(Long id, @NotBlank String name, @NotBlank String description, @NotNull Boolean available,
                             UserDto owner, Collection<CommentDto> comments) {
        super(id, name, description, available);
        this.comments = comments;
        this.owner = owner;
    }
}
