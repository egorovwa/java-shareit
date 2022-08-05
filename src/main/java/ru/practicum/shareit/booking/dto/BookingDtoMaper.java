package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.ItemServise;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class BookingDtoMaper {
    private final ItemServise itemServise; // TODO: 03.08.2022 вроде не удачно


    public BookingDto toDtoCreated(Booking booking) {
        return new BookingDto(booking.getId(),
                LocalDateTime.ofEpochSecond(booking.getStart(),0,ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(booking.getEnd(),0,ZoneOffset.UTC),
                booking.getItem(),booking.getBooker(),booking.getStatus());
    }
    public BookingDto toDto(Booking booking){
        return new BookingDto(booking.getId(),
                LocalDateTime.ofEpochSecond(booking.getStart(),0,ZoneOffset.UTC),
                LocalDateTime.ofEpochSecond(booking.getEnd(),0,ZoneOffset.UTC),
                booking.getItem(),booking.getBooker(),booking.getStatus());
    }

}
