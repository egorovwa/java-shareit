package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServise;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServise;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiseImpl implements BookingServise {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(Booking booking, long userId) throws IncorrectUserIdException {
        if (booking.getBooker().getId() == userId) {
            log.info("Добавлен запрос на бронирование пользователем id= {} вещи id= {}", userId, booking.getItem().getName());
            return bookingRepository.save(booking);

        } else if (booking.getBooker() == null) {
            booking.setBooker(userRepository.findById(userId)
                    .orElseThrow(() -> new IncorrectUserIdException("Пользователь не существует", String.valueOf(userId))));
            log.info("Добавлен запрос на бронирование пользователем id= {} вещи id= {}", userId, booking.getItem().getName());
            return bookingRepository.save(booking);
        } else {
            log.warn("Попытка создания запроса на бронирование ползователем id {} для пользователя id {}",
                    userId, booking.getBooker().getId());
            throw new IncorrectUserIdException("не верный id пользователя", String.valueOf(userId));
        }
    }

    @Override
    public Booking setStatus(int userId, int itemId, BookingStatus status) {
        return null;
    }
}
