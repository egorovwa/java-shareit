package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.contract.booking.dto.BookItemRequestDto;
import ru.practicum.contract.booking.dto.BookingState;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;

import java.util.Collection;
import java.util.Optional;

public interface BookingServise {
    Booking createBooking(BookItemRequestDto bookItemRequestDto, long userId) throws ModelNotExitsException, TimeIntersectionException, ItemNotAvalibleExxeption;

    Booking setStatus(long useId, Long bookingId, Boolean approved) throws IncorrectUserIdException, ParametrNotFoundException, StatusAlredyException;

    Booking findById(long bookingId, long useId) throws ModelNotExitsException, IncorrectUserIdException;

    Collection<Booking> getAllUser(long useId, Pageable pageable) throws UserNotFoundExteption;

    Collection<Booking> getAllUser(long useId, BookingState state, Pageable pageParam) throws UserNotFoundExteption, UnknownStateException;

    Collection<Booking> getAllOwner(long useId, Pageable pageable, BookingState state) throws UserNotFoundExteption, UnknownStateException;

    Collection<Booking> getAllOwner(long useId, Pageable pageParam) throws UserNotFoundExteption;

    Optional<Booking> findLastBookingToItem(long itemId);

    Optional<Booking> findNextBookingToItem(long itemId);

}
