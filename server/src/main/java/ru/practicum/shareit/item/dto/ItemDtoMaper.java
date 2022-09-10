package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.contract.item.dto.CommentDto;
import ru.practicum.contract.item.dto.ItemDto;
import ru.practicum.contract.item.dto.ItemDtoCreated;
import ru.practicum.contract.item.dto.ItemDtoWithBoking;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoMaper;

import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ItemDtoMaper {
    private final BookingDtoMaper bookingDtoMaper;
    private final UserDtoMaper userDtoMaper;

    public ItemDtoCreated toDtoCreated(Item item) {
        if (item.getRequest() != null) {
            return new ItemDtoCreated(item.getId(), item.getName(),
                    item.getDescription(), item.getAvailable(), userDtoMaper.toDto(item.getOwner()),
                    item.getRequest().getId());
        } else {
            return new ItemDtoCreated(item.getId(), item.getName(),
                    item.getDescription(), item.getAvailable(), userDtoMaper.toDto(item.getOwner()), null);
        }
    }

    public Item fromDto(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                null, null);
    }

    public ItemDto toDto(Item item) {
        if (item.getRequest() != null) {
            return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                    item.getRequest().getId());
        } else {
            return new ItemDto(item.getId(), item.getName(),
                    item.getDescription(), item.getAvailable());
        }

    }

    public ItemDtoWithBoking toDtoWithBooking(Item item, Optional<Booking> last, Optional<Booking> next,
                                              Collection<CommentDto> comments) {
        return new ItemDtoWithBoking(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                userDtoMaper.toDto(item.getOwner()), bookingDtoMaper.toItemDto(last),
                bookingDtoMaper.toItemDto(next), comments);
    }

    public ItemDtoWithBoking toDtoWithBooking(Item item, Collection<CommentDto> comments) {
        return new ItemDtoWithBoking(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                userDtoMaper.toDto(item.getOwner()), comments);
    }

}
