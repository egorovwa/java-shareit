package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.practicum.contract.booking.dto.BookItemRequestDto;
import ru.practicum.contract.booking.dto.BookingState;
import ru.practicum.contract.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServise;
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
import java.util.Comparator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingServiseImpl implements BookingServise {
    private final BookingRepository bookingRepository;
    private final UserServise userServise;
    private final ItemServise itemServise;

    @Override
    public Booking createBooking(BookItemRequestDto dto, long userId) throws ModelNotExitsException,
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
        userServise.findById(useId);
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
    public Collection<Booking> getAllUser(long useId, Pageable pageParam) throws UserNotFoundExteption {
        if (pageParam == null) {
            try {
                userServise.findById(useId);
            } catch (ModelNotExitsException e) {
                log.warn("Запрос на лист бронировани от несуществующего пользователя id {}", useId);
                throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
            }
            log.info("Передача листа бронирований пользователя id {}", useId);
            return bookingRepository.findByBooker_IdOrderByStartDesc(useId);
        } else {

            try {
                userServise.findById(useId);

            } catch (ModelNotExitsException e) {
                log.warn("Запрос на лис бронировани от несуществующего пользователя id {}", useId);
                throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
            }
            log.info("Передача листа бронирований пользователя id {}", useId);
            return bookingRepository.findByBooker_IdOrderByStartDesc(pageParam, useId).toList();

        }
    }

    @Override
    public Collection<Booking> getAllUser(long useId, BookingState state, Pageable pageable) throws UserNotFoundExteption, UnknownStateException {
        try {
            userServise.findById(useId);
        } catch (ModelNotExitsException e) {
            log.warn("Запрос на листа бронировани от несуществующего пользователя id {}", useId);
            throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
        }
        switch (state) {
            case ALL:
                return getAllUser(useId, pageable);
            case PAST:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.PAST);
                return bookingRepository.findByBookerIdStatePast(useId,
                        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            case WAITING:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.WAITING);
                return bookingRepository.findByBooker_IdAndStatus(useId, BookingStatus.WAITING);
            case CURRENT:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.CURRENT);
                return bookingRepository.findByBookerIdStateCurrent(useId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            case REJECTED:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.REJECTED);
                return bookingRepository.findByBooker_IdAndStatus(useId, BookingStatus.REJECTED);
            case FUTURE:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.FUTURE);
                return bookingRepository.findFuture(useId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
            default:
                log.warn("Запрос на несуществующий статус");
                throw new UnknownStateException("неизвестный state", state.toString());
        }
    }

    @Override
    public Collection<Booking> getAllOwner(long useId, Pageable pageable, BookingState state) throws UserNotFoundExteption, UnknownStateException {
        try {
            userServise.findById(useId);
        } catch (ModelNotExitsException e) {
            log.warn("Запрос на листа бронировани от несуществующего пользователя id {}", useId);
            throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
        }
        switch (state) {
            case ALL:
                log.info("Передача листа бронирований пользователя id {}", useId);
                return bookingRepository.findOwnerAll(pageable, useId).toList();
            case FUTURE:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.FUTURE);
                return bookingRepository.findOwnerFuture(useId, pageable,
                        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).toList();
            case CURRENT:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.CURRENT);
                return bookingRepository.findOwnerCurrent(useId, pageable,
                        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).toList();
            case WAITING:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.WAITING);
                return bookingRepository.findByOwnerIdAndStatus(useId, pageable, BookingStatus.WAITING).toList();
            case PAST:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.PAST);
                return bookingRepository.findOwnerPast(useId, pageable,
                        LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)).toList();
            case REJECTED:
                log.info("Передача листа бронирований пользователя id {}, status = {}", useId, BookingState.REJECTED);
                return bookingRepository.findByOwnerIdAndStatus(useId, pageable, BookingStatus.REJECTED).toList();
            default:
                log.warn("Запрос на несуществующий статус");
                throw new UnknownStateException("неизвестный state", state.toString());
        }
    }

    @Override
    public Collection<Booking> getAllOwner(long useId, Pageable pageable) throws UserNotFoundExteption {
        if (pageable == null) {
            try {
                userServise.findById(useId);
            } catch (ModelNotExitsException e) {
                log.warn("Запрос на листа бронировани от несуществующего пользователя id {}", useId);
                throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
            }
            log.info("Передача листа бронирований пользователя id {}", useId);
            return bookingRepository.findOwnerAll(useId);
        } else {

            try {
                userServise.findById(useId);
            } catch (ModelNotExitsException e) {
                log.warn("Запрос на листа бронировани от несуществующего пользователя id {}", useId);
                throw new UserNotFoundExteption(e.getMessage(), e.getParam(), e.getValue());
            }
            log.info("Передача листа бронирований пользователя id {}", useId);
            return bookingRepository.findOwnerAll(pageable, useId).toList();
        }
    }

    private boolean timeValidation(Long start, Long end) {
        return end - start > 0 && start > LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public Optional<Booking> findLastBookingToItem(long itemId) {
        return bookingRepository.findLastBookingToItem(itemId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .stream()
                .max(Comparator.comparing(Booking::getEnd));
    }

    @Override
    public Optional<Booking> findNextBookingToItem(long itemId) {
        return bookingRepository.findNextBookingToItem(itemId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .stream()
                .min(Comparator.comparingLong(Booking::getStart));
    }


}
