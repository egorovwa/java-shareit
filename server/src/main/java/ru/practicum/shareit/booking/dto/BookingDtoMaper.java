package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.contract.booking.dto.BookingDto;
import ru.practicum.contract.booking.dto.BookingDtoToItem;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.user.dto.UserDtoMaper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingDtoMaper {
    private final UserDtoMaper userDtoMaper = new UserDtoMaper();
    private final ItemDtoMaper itemDtoMaper = new ItemDtoMaper(this, userDtoMaper);

    public BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC),
                itemDtoMaper.toDto(booking.getItem()), userDtoMaper.toDto(booking.getBooker()),
                booking.getStatus());
    }

    public BookingDtoToItem toItemDto(Optional<Booking> mayBebooking) {
        if (mayBebooking.isPresent()) {
            Booking booking = mayBebooking.get();
            return new BookingDtoToItem(booking.getId(),
                    LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                    LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC),
                    booking.getItem().getId(),
                    booking.getBooker().getId(),
                    booking.getStatus());
        } else {
            return null;
        }
    }

}
