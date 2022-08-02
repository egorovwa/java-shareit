package ru.practicum.shareit.booking;

import ru.practicum.shareit.exceptions.IncorrectUserIdException;

public interface BookingServise {
    Booking createBooking(Booking booking, long useId) throws IncorrectUserIdException;
    Booking setStatus(int userId, int itemId, BookingStatus status);
}
