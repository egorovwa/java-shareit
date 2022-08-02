package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;

/**
 * // TODO .
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServise bookingServise;
    private final BookingDtoMaper dtoMaper;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long useId,
                                    @RequestBody BookingDto bookingDto) throws IncorrectUserIdException {
return dtoMaper.toDto(bookingServise.createBooking(dtoMaper.fromDto(bookingDto),useId));
    }
}
