package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

@Component
public class ItemDtoMaper {
    public static Item fromDto(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), itemDto.getOwner());
    }

    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getOwner());
    }
    public static ItemDtoWithBoking toDtoWithBooking(Item item, Optional<Booking> last, Optional<Booking> next,
                                                     Collection<CommentDto> comments){
        return new ItemDtoWithBoking(item.getId(),item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(), BookingDtoMaper.toItemDto(last),
                BookingDtoMaper.toItemDto(next),comments);
    }
    public static ItemDtoWithBoking toDtoWithBooking(Item item,Collection<CommentDto> comments){
        return new ItemDtoWithBoking(item.getId(),item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(),comments);
    }

}
