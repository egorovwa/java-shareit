package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;

import java.time.LocalDate;

public class BookingDtoMaper {
    public Booking fromDto(BookingDto dto) {
        return new Booking(dto.getId(),
                dto.getStart().toEpochDay(),
                dto.getEnd().toEpochDay(),
                dto.getItem(),
                dto.getBooker(),
                dto.getStatus());
    }

    public BookingDto toDto(Booking booking) {
        return new BookingDto(booking.getId(),
                LocalDate.ofEpochDay(booking.getStart()),
                LocalDate.ofEpochDay(booking.getEnd()),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }
}
