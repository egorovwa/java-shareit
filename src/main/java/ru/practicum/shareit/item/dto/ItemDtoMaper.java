package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemDtoMaper {
    private final BookingDtoMaper bookingDtoMaper;

    public Item fromDto(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), itemDto.getOwner());
    }

    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable(), item.getOwner());
    }

    public ItemDtoWithBoking toDtoWithBooking(Item item, Optional<Booking> last, Optional<Booking> next,
                                              Collection<CommentDto> comments) {
        return new ItemDtoWithBoking(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(), bookingDtoMaper.toItemDto(last),
                bookingDtoMaper.toItemDto(next), comments);
    }

    public ItemDtoWithBoking toDtoWithBooking(Item item, Collection<CommentDto> comments) {
        return new ItemDtoWithBoking(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(), comments);
    }

}
