package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;

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
    public static ItemDtoWithBoking toDtoWithBooking(Item item, Booking last, Booking next){
        return new ItemDtoWithBoking(item.getId(),item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner(),last,next);
    }
    public static ItemDtoWithBoking toDtoWithBooking(Item item){
        return new ItemDtoWithBoking(item.getId(),item.getName(), item.getDescription(), item.getAvailable(),
                item.getOwner());
    }

}
