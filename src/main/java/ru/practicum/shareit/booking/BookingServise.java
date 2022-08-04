package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;

import java.util.Collection;

public interface BookingServise {
    Booking createBooking(Booking booking, long useId) throws IncorrectUserIdException, ModelNotExitsException, TimeIntersectionException, ItemNotAvalibleExxeption;
    Booking setStatus(long useId, Long bookingId, Boolean approved) throws IncorrectUserIdException, ParametrNotFoundException, StatusAlredyException;

    Booking findById(long bookingId, long useId) throws ModelNotExitsException, IncorrectUserIdException;

    Collection<Booking> getAllUser(long useId) throws UserNotFoundExteption;
    Collection<Booking> getAllUser(long useId, BookingState state) throws UserNotFoundExteption, UnknownStateException;

    Collection<Booking> getAllOwner(long useId, BookingState state) throws UserNotFoundExteption, UnknownStateException;

    Collection<Booking> getAllOwner(long useId) throws UserNotFoundExteption;
}
