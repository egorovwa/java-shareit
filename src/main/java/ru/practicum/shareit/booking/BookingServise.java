package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.util.PageParam;

import java.util.Collection;
import java.util.Optional;

public interface BookingServise {
    Booking createBooking(BookingDtoToCreate bookingDtoToCreate, long userId) throws ModelNotExitsException, TimeIntersectionException, ItemNotAvalibleExxeption;

    Booking setStatus(long useId, Long bookingId, Boolean approved) throws IncorrectUserIdException, ParametrNotFoundException, StatusAlredyException;

    Booking findById(long bookingId, long useId) throws ModelNotExitsException, IncorrectUserIdException;

    Collection<Booking> getAllUser(long useId, PageParam pageParam) throws UserNotFoundExteption;

    Collection<Booking> getAllUser(long useId, BookingState state, PageParam pageParam) throws UserNotFoundExteption, UnknownStateException;

    Collection<Booking> getAllOwner(long useId, BookingState state) throws UserNotFoundExteption, UnknownStateException;

    Collection<Booking> getAllOwner(long useId, PageParam pageParam) throws UserNotFoundExteption;

    Optional<Booking> findLastBookingToItem(long itemId);

    Optional<Booking> findNextBookingToItem(long itemId);

}
