package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiseImpl implements BookingServise {
    private final BookingRepository bookingRepository;
    private final UserServise userServise;
    private final ItemServise itemServise;

    @Override
    public Booking createBooking(BookingDtoToCreate dto, long userId) throws IncorrectUserIdException, ModelNotExitsException,
            TimeIntersectionException, ItemNotAvalibleExxeption {
        Item item = itemServise.findById(dto.getItemId());
        User owner = item.getOwner();
        User user = userServise.findById(userId);
        if (user.equals(owner)) {
            throw new ModelNotExitsException("Вещь принадлежит пользователю", "item owner id",
                    String.valueOf(item.getOwner().getId()));
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvalibleExxeption("Вещь занята", String.valueOf(item.getId()));
        }
        Booking booking = new Booking(null, dto.getStart().toEpochSecond(ZoneOffset.UTC),
                dto.getEnd().toEpochSecond(ZoneOffset.UTC), item, user, BookingStatus.WAITING);
        if (!timeValidation(booking.getStart(), booking.getEnd())) {
            throw new TimeIntersectionException("Время начала окончания невены", dto.getStart(), dto.getEnd());
        }
        return bookingRepository.save(booking);

    }

    @Override
    public Booking setStatus(long useId, Long bookingId, Boolean approved) throws IncorrectUserIdException, ParametrNotFoundException, StatusAlredyException {
        if (approved == null) {
            throw new ParametrNotFoundException("approved = null", "approved");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Неверный id бронирования"));
        if (booking.getStatus().equals(BookingStatus.WAITING)) {
            if (booking.getItem().getOwner().getId() == useId) {
                booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
                log.info("Бронирование id = {} {}", bookingId, booking.getStatus().toString());
                return bookingRepository.save(booking);
            } else {
                log.warn("Попытка изменения статуса пользователем id ={} , бронирования id = {} .", useId, bookingId);
                throw new IncorrectUserIdException("не принадлежит пользователю", String.valueOf(useId));
            }
        } else {
            throw new StatusAlredyException("Статус Уже установлен", String.valueOf(bookingId));
        }
    }

    @Override
    public Booking findById(long bookingId, long useId) throws ModelNotExitsException, IncorrectUserIdException {
        User finder = userServise.findById(useId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ModelNotExitsException("Бронирование не найдено", "bookingId",
                        String.valueOf(bookingId)));
        if (useId == booking.getBooker().getId() || useId == booking.getItem().getOwner().getId()) {
            return booking;
        } else {
            throw new IncorrectUserIdException("Бронирование не относится к пользователю", String.valueOf(useId));
        }

    }

    @Override
    public Collection<Booking> getAllUser(long useId) throws UserNotFoundExteption {
        try {
            userServise.findById(useId);
        } catch (ModelNotExitsException e) {
            throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
        }
        return bookingRepository.findByBooker_IdOrderByStartDesc(useId);
    }

    @Override
    public Collection<Booking> getAllUser(long useId, BookingState state) throws UserNotFoundExteption, UnknownStateException {
        try {
            userServise.findById(useId);
        } catch (ModelNotExitsException e) {
            throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
        }
        switch (state) {
            case ALL:
                return getAllUser(useId);
            case PAST:
                return bookingRepository.findByBookerIdStatePast(useId,
                        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            case WAITING:
                return bookingRepository.findByBooker_IdAndStatus(useId, BookingStatus.WAITING);
            case CURRENT:
                return bookingRepository.findByBookerIdStateCurrent(useId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatus(useId, BookingStatus.REJECTED);
            case FUTURE:
                return bookingRepository.findFuture(useId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            default:
                throw new UnknownStateException("неизвестный state", state.toString());
        }
    }

    @Override
    public Collection<Booking> getAllOwner(long useId, BookingState state) throws UserNotFoundExteption, UnknownStateException {
        try {
            userServise.findById(useId);
        } catch (ModelNotExitsException e) {
            throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
        }
        switch (state) {
            case ALL:
                return bookingRepository.findOwnerAll(useId);
            case FUTURE:
                return bookingRepository.findOwnerFuture(useId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            case CURRENT:
                return bookingRepository.findOwnerCurrent(useId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            case WAITING:
                return bookingRepository.findByOwnerIdAndStatus(useId, BookingStatus.WAITING);
            case PAST:
                return bookingRepository.findOwnerPast(useId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            case REJECTED:
                return bookingRepository.findByOwnerIdAndStatus(useId, BookingStatus.REJECTED);
            default:
                throw new UnknownStateException("неизвестный state", state.toString());
        }
    }

    @Override
    public Collection<Booking> getAllOwner(long useId) throws UserNotFoundExteption {
        try {
            userServise.findById(useId);
        } catch (ModelNotExitsException e) {
            throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
        }
        return bookingRepository.findOwnerAll(useId);
    }

    private boolean timeValidation(Long start, Long end) {
        return end - start > 0 && start > LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                && end > LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public Booking findLastBookingToItem(long itemId) {
        return bookingRepository.findLastBookingToItem(itemId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).get();
    }

    @Override
    public Booking findNextBookingToItem(long itemId) {
        return bookingRepository.findNextBookingToItem(itemId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).get();
    }
}
