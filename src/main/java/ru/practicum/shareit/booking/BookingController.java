package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.booking.dto.StateDtoMaper;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.websocket.server.PathParam;
import java.util.Collection;
import java.util.stream.Collectors;

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
                                            @RequestBody @Valid BookingDtoToCreate bookingDtoToCreate)
            throws ModelNotExitsException, TimeIntersectionException, ItemNotAvalibleExxeption {
        return BookingDtoMaper.toDtoCreated(bookingServise.createBooking(bookingDtoToCreate, useId));
    }

    @PatchMapping("/{bookingId}")
    @Valid
    public BookingDto bookingСonfirmation(@RequestHeader("X-Sharer-User-Id") long useId,
                                          @PathVariable Long bookingId,
                                          @PathParam("approved") @NotNull Boolean approved) throws IncorrectUserIdException, ParametrNotFoundException, StatusAlredyException {
        return BookingDtoMaper.toDto(bookingServise.setStatus(useId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long useId) throws ModelNotExitsException, IncorrectUserIdException {
        return BookingDtoMaper.toDto(bookingServise.findById(bookingId, useId));
    }

    @GetMapping
    public Collection<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") long useId,
                                         @PathParam("state") String state) throws UnknownStateException, UserNotFoundExteption {
        if (state == null) {
            return bookingServise.getAllUser(useId).stream().map(BookingDtoMaper::toDto).collect(Collectors.toList());
        } else {
            return bookingServise.getAllUser(useId, StateDtoMaper.fromDto(state)).stream().map(BookingDtoMaper::toDto).collect(Collectors.toList());
        }
    }
    @GetMapping("/owner")
    public Collection<BookingDto> getAllOwner(@RequestHeader("X-Sharer-User-Id") long useId,
                                              @PathParam("state") String state) throws UnknownStateException, UserNotFoundExteption {
        if (state==null){
            return bookingServise.getAllOwner(useId).stream().map(BookingDtoMaper::toDto).collect(Collectors.toList());
        }else {
            return bookingServise.getAllOwner(useId,StateDtoMaper.fromDto(state)).stream().map(BookingDtoMaper::toDto).collect(Collectors.toList());
        }
    }
}
