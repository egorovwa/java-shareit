package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.ItemServise;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookingDtoMaper {
    private final ItemServise itemServise; // TODO: 03.08.2022 вроде не удачно


    public static BookingDto toDtoCreated(Booking booking) {
        return new BookingDto(booking.getId(),
                LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC),
                booking.getItem(), booking.getBooker(), booking.getStatus());
    }

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC),
                booking.getItem(), booking.getBooker(), booking.getStatus());
    }

    public static BookingDtoToItem toItemDto(Optional<Booking> mayBebooking) {
        if (mayBebooking.isPresent()) {
            Booking booking = mayBebooking.get();
            return new BookingDtoToItem(booking.getId(),
                    LocalDateTime.ofEpochSecond(booking.getStart(), 0, ZoneOffset.UTC),
                    LocalDateTime.ofEpochSecond(booking.getEnd(), 0, ZoneOffset.UTC),
                    booking.getItem().getId(),
                    booking.getBooker().getId(),
                    booking.getStatus());
        }else {
            return null;
        }
    }

}
