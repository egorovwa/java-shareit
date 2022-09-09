package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.booking.dto.StateDtoMaper;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectPageValueException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.util.PageParam;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.Collection;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServise bookingServise;
    private final BookingDtoMaper dtoMaper;

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") long useId,
                                    @RequestBody @Valid BookItemRequestDto bookItemRequestDto)
            throws ModelNotExitsException, TimeIntersectionException, ItemNotAvalibleExxeption {
        return dtoMaper.toDtoCreated(bookingServise.createBooking(bookItemRequestDto, useId));
    }

    @SuppressWarnings("NonAsciiCharacters")
    @PatchMapping("/{bookingId}")
    @Valid
    public BookingDto bookingСonfirmation(@RequestHeader("X-Sharer-User-Id") long useId,
                                          @PathVariable Long bookingId,
                                          @PathParam("approved") @NotNull Boolean approved) throws IncorrectUserIdException,
            ParametrNotFoundException, StatusAlredyException {
        return dtoMaper.toDto(bookingServise.setStatus(useId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@PathVariable long bookingId, @RequestHeader("X-Sharer-User-Id") long useId) throws ModelNotExitsException, IncorrectUserIdException {
        return dtoMaper.toDto(bookingServise.findById(bookingId, useId));
    }

    @GetMapping
    public Collection<BookingDto> getAll(@RequestHeader("X-Sharer-User-Id") long useId,
                                         @PathParam("state") String state,
                                         @PathParam("from") Integer from,
                                         @PathParam("size") Integer size) throws UnknownStateException, UserNotFoundExteption, IncorrectPageValueException {

        if (state == null) {

            return bookingServise.getAllUser(useId, PageParam.createPageable(from, size)).stream().map(dtoMaper::toDto).collect(Collectors.toList());
        } else {
            return bookingServise.getAllUser(useId, StateDtoMaper.fromDto(state), PageParam.createPageable(from, size)).stream().map(dtoMaper::toDto).collect(Collectors.toList());
        }
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllOwner(@RequestHeader("X-Sharer-User-Id") long useId,
                                              @PathParam("state") String state,
                                              @PathParam("from") Integer from,
                                              @PathParam("size") Integer size) throws UnknownStateException, UserNotFoundExteption, IncorrectPageValueException {
        if (state == null) {

            return bookingServise.getAllOwner(useId, PageParam.createPageable(from, size)).stream().map(dtoMaper::toDto).collect(Collectors.toList());
        } else {
            return bookingServise.getAllOwner(useId, PageParam.createPageable(from, size), StateDtoMaper.fromDto(state)).stream().map(dtoMaper::toDto).collect(Collectors.toList());
        }
    }
}
