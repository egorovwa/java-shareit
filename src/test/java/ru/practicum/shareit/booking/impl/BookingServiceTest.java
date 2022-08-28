package ru.practicum.shareit.booking.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserServise userServise;
    @Mock
    ItemServise itemServise;
    @InjectMocks
    BookingServiseImpl bookingServise;

    @BeforeEach
    void clear() {
        Mockito.clearAllCaches();
    }


    @Test
    void test1_1createBooking_itemNotFound() throws ModelNotExitsException {
        Mockito
                .when(itemServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(ModelNotExitsException.class, () ->
                bookingServise.createBooking(BOOKING_DTO_TO_CREATE_ITEM1, 1L));
    }

    @Test
    void test1_2createBooking_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(itemServise.findById(1L))
                .thenReturn(ITEM_ID1_OWNER1_AVALIBLE_TRUE);
        Mockito
                .when(userServise.findById(2L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(ModelNotExitsException.class, () ->
                bookingServise.createBooking(BOOKING_DTO_TO_CREATE_ITEM1, 2L));
    }

    @Test
    void test1_3createBooking_userIsOwner() throws ModelNotExitsException {
        Mockito
                .when(itemServise.findById(1L))
                .thenReturn(ITEM_ID1_OWNER1_AVALIBLE_TRUE);
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        assertThrows(ModelNotExitsException.class, () ->
                bookingServise.createBooking(BOOKING_DTO_TO_CREATE_ITEM1, 1L));

    }

    @Test
    void test1_4createBooking_itemAvailableFalse() throws ModelNotExitsException {
        Item item = ITEM_ID1_OWNER1_AVALIBLE_TRUE;
        item.setAvailable(false);
        Mockito
                .when(itemServise.findById(1L))
                .thenReturn(item);
        Mockito
                .when(userServise.findById(2L))
                .thenReturn(USER_ID2);
        assertThrows(ItemNotAvalibleExxeption.class, () ->
                bookingServise.createBooking(BOOKING_DTO_TO_CREATE_ITEM1, 2L));
    }

    @Test
    void test1_5createBooking_TimeIntersection_whenTimeLast() throws ModelNotExitsException {
        User user = USER_ID2;
        Mockito
                .when(itemServise.findById(1L))
                .thenReturn(ITEM_ID1_OWNER1_AVALIBLE_TRUE);
        Mockito
                .when(userServise.findById(2L))
                .thenReturn(USER_ID2);
        assertThrows(TimeIntersectionException.class, () ->
                bookingServise.createBooking(BOOKING_DTO_TO_CREATE_ITEM1, 2));
    }

    @Test
    void test1_7createBooking_TimeIntersection_startAfteEnd() throws ModelNotExitsException {
        User user = USER_ID2;
        BookingDtoToCreate toCreate = new BookingDtoToCreate(LocalDateTime.now().plus(Duration.ofMinutes(5)),
                LocalDateTime.now().plus(Duration.ofMinutes(1)), 1L);
        Mockito
                .when(itemServise.findById(1L))
                .thenReturn(ITEM_ID1_OWNER1_AVALIBLE_TRUE);
        Mockito
                .when(userServise.findById(2L))
                .thenReturn(USER_ID2);
        assertThrows(TimeIntersectionException.class, () ->
                bookingServise.createBooking(BOOKING_DTO_TO_CREATE_ITEM1, 2));
    }

    @Test
    void test1_6createBooking() throws ModelNotExitsException, ItemNotAvalibleExxeption, TimeIntersectionException {
        Item item = ITEM_ID1_OWNER1_AVALIBLE_TRUE;
        User user = USER_ID2;
        BookingDtoToCreate toCreate = new BookingDtoToCreate(LocalDateTime.now().plus(Duration.ofMinutes(1)),
                LocalDateTime.now().plus(Duration.ofHours(1)), 1L);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setStart(toCreate.getStart().toEpochSecond(ZoneOffset.UTC));
        booking.setEnd(toCreate.getEnd().toEpochSecond(ZoneOffset.UTC));
        booking.setBooker(USER_ID2);
        booking.setStatus(BookingStatus.WAITING);
        Mockito
                .when(itemServise.findById(1L))
                .thenReturn(item);
        Mockito
                .when(userServise.findById(2L))
                .thenReturn(USER_ID2);

        bookingServise.createBooking(toCreate, 2L);
        Mockito
                .verify(bookingRepository, Mockito.times(1)).save(booking);
    }

    @Test
    void test2_1_setStatus_approvedNull() {
        assertThrows(ParametrNotFoundException.class, () -> bookingServise.setStatus(1L, 1L, null));
    }

    @Test
    void test2_2_setStatus_bookingNotFound() {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> bookingServise.setStatus(1L, 1L, true));
    }

    @Test
    void test2_3_setStatus_statusNotWaiting() {

        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(BOOKING1_USER2_ITEM1_APPROVED));
        assertThrows(StatusAlredyException.class, () -> bookingServise.setStatus(1L, 1L, true));
    }

    @Test
    void test2_4_setStatus_userNotOwner() {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                        LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC), ITEM_ID1_OWNER1_AVALIBLE_TRUE,
                        USER_ID2, BookingStatus.WAITING)));
        assertThrows(IncorrectUserIdException.class, () -> bookingServise.setStatus(2L, 1L, true));
    }

    @Test
    void test2_5_setStatus_APPROVED() throws IncorrectUserIdException, ParametrNotFoundException, StatusAlredyException {
        Booking booking = new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC),
                ITEM_ID1_OWNER1_AVALIBLE_TRUE, USER_ID2, BookingStatus.WAITING);
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(booking));
        bookingServise.setStatus(1L, 1L, true);
        Mockito
                .verify(bookingRepository, Mockito.times(1)).save(booking);

    }

    @Test
    void test2_6_setStatus_REJECTED() throws IncorrectUserIdException, ParametrNotFoundException, StatusAlredyException {
        Booking result = new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC), ITEM_ID1_OWNER1_AVALIBLE_TRUE,
                USER_ID2, BookingStatus.REJECTED);
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                        LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC), ITEM_ID1_OWNER1_AVALIBLE_TRUE,
                        USER_ID2, BookingStatus.WAITING)));
        bookingServise.setStatus(1L, 1L, false);
        Mockito
                .verify(bookingRepository, Mockito.times(1)).save(result);
    }

    @Test
    void test3_1_findById_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(ModelNotExitsException.class, () -> bookingServise.findById(1L, 1L));
    }

    @Test
    void test3_3_findById_bookingNotFound() {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotExitsException.class, () -> bookingServise.findById(1L, 1L));
    }

    @Test
    void test3_4_findById_userNotOwnerOrCreater() {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(BOOKING1_USER2_ITEM1_WAITING));
        assertThrows(IncorrectUserIdException.class, () -> bookingServise.findById(1L, 3L));
    }

    @Test
    void test3_5findById_owner() throws ModelNotExitsException, IncorrectUserIdException {
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(BOOKING1_USER2_ITEM1_WAITING));
        Booking booking = bookingServise.findById(1L, 1L);
        assertThat(booking, is(BOOKING1_USER2_ITEM1_WAITING));
    }

    @Test
    void test3_6findById_booker() throws ModelNotExitsException, IncorrectUserIdException {
        Mockito
                .when(userServise.findById(2L))
                .thenReturn(USER_ID2);
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(BOOKING1_USER2_ITEM1_WAITING));
        Booking booking = bookingServise.findById(1L, 2L);
        assertThat(booking, is(BOOKING1_USER2_ITEM1_WAITING));
    }


    @Test
    void test4_1_getAllUser_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(UserNotFoundExteption.class, () -> bookingServise.getAllUser(1L, 0, 5));
    }

    @Test
    void test4_2_getAllUser_userNotFound_withOutFromSize() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(UserNotFoundExteption.class, () -> bookingServise.getAllUser(1L, null, null));
    }

    @Test
    void test4_3_getAllUser_withOutFromSize() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(bookingRepository.findByBooker_IdOrderByStartDesc(1L))
                .thenReturn(List.of(BOOKING1_USER2_ITEM1_WAITING));
        bookingServise.getAllUser(1L, null, null);
        Mockito.verify(bookingRepository, Mockito.times(1)).findByBooker_IdOrderByStartDesc(1L);
    }

    @Test
    void test4_4_getAllUser_withFromSize() throws ModelNotExitsException {
        Pageable pageable = PageRequest.of(0, 5);
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(bookingRepository.findByBooker_IdOrderByStartDesc(pageable, 1L))
                .thenReturn(new PageImpl<>(List.of(BOOKING1_USER2_ITEM1_WAITING)));
        bookingServise.getAllUser(1L, 0, 5);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_IdOrderByStartDesc(pageable, 1L);
    }

    @Test
    void test5_1_getAllUser_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(UserNotFoundExteption.class, () -> bookingServise.getAllUser(1L, BookingState.FUTURE, 0, 5));
    }

    @Test
    void test5_7_testGetAllUser_ALL() throws UnknownStateException, ModelNotExitsException {
        Pageable pageable = PageRequest.of(0, 5);
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(bookingRepository.findByBooker_IdOrderByStartDesc(pageable, 1L))
                .thenReturn(new PageImpl<>(List.of(BOOKING1_USER2_ITEM1_WAITING)));
        bookingServise.getAllUser(1L, BookingState.ALL, 0, 5);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_IdOrderByStartDesc(pageable, 1L);
    }

    @Test
    void test5_2_testGetAllUser_PAST() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllUser(1L, BookingState.PAST, 0, 5);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdStatePast(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    void test5_3_testGetAllUser_WAITING() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllUser(1L, BookingState.WAITING, 0, 5);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_IdAndStatus(1L, BookingStatus.WAITING);
    }

    @Test
    void test5_4_testGetAllUser_CURRENT() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllUser(1L, BookingState.CURRENT, 0, 5);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdStateCurrent(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    void test5_5_testGetAllUser_REJECTED() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllUser(1L, BookingState.REJECTED, 0, 5);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBooker_IdAndStatus(1L, BookingStatus.REJECTED);
    }

    @Test
    void test5_6_testGetAllUser_FUTURE() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllUser(1L, BookingState.FUTURE, 0, 5);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFuture(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    void test6_1_getAllOwner_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(UserNotFoundExteption.class, () -> bookingServise.getAllOwner(1L, BookingState.FUTURE));
    }

    @Test
    void test6_2_getAllOwner_PAST() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllOwner(1L, BookingState.PAST);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findOwnerPast(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    void test6_3_getAllOwner_WAITING() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllOwner(1L, BookingState.WAITING);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByOwnerIdAndStatus(1L, BookingStatus.WAITING);
    }

    @Test
    void test6_4_getAllOwner_CURRENT() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllOwner(1L, BookingState.CURRENT);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findOwnerCurrent(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    void test6_5_getAllOwner_REJECTED() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllOwner(1L, BookingState.REJECTED);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByOwnerIdAndStatus(1L, BookingStatus.REJECTED);
    }

    @Test
    void test6_6_getAllOwner_FUTURE() throws UnknownStateException, UserNotFoundExteption {
        bookingServise.getAllOwner(1L, BookingState.FUTURE);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findOwnerFuture(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    }

    @Test
    void test6_7_getAllOwner_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(UserNotFoundExteption.class, () -> bookingServise.getAllOwner(1L, 0, 5));
    }

    @Test
    void test6_8_getAllOwner_userNotFound_withOutFromSize() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(UserNotFoundExteption.class, () -> bookingServise.getAllOwner(1L, null, null));
    }

    @Test
    void test6_9_getAllUser_withOutFromSize() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(bookingRepository.findOwnerAll(1L))
                .thenReturn(List.of(BOOKING1_USER2_ITEM1_WAITING));
        bookingServise.getAllOwner(1L, null, null);
        Mockito.verify(bookingRepository, Mockito.times(1)).findOwnerAll(1L);
    }

    @Test
    void test6_10_getAllUser_withFromSize() throws ModelNotExitsException {
        Pageable pageable = PageRequest.of(0, 5);
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(bookingRepository.findOwnerAll(pageable, 1L))
                .thenReturn(new PageImpl<>(List.of(BOOKING1_USER2_ITEM1_WAITING)));
        bookingServise.getAllOwner(1L, 0, 5);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findOwnerAll(pageable, 1L);
    }

    @Test
    void test6_11_testGetAllUser_ALL() throws UnknownStateException, ModelNotExitsException {
        Pageable pageable = PageRequest.of(0, 5);
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(bookingRepository.findOwnerAll(1L))
                .thenReturn(List.of(BOOKING1_USER2_ITEM1_WAITING));
        bookingServise.getAllOwner(1L, BookingState.ALL);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findOwnerAll(1L);
    }

    @Test
    void test7_findLastBookingToItem() {
        long time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        Mockito
                .when(bookingRepository.findLastBookingToItem(1L, time))
                .thenReturn(List.of(BOOKING_FIST));
        bookingServise.findLastBookingToItem(1L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findLastBookingToItem(1L, time);
    }

    @Test
    void test8_findNextBookingToItem() {
        long time = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        Mockito
                .when(bookingRepository.findNextBookingToItem(1L, time))
                .thenReturn(List.of(BOOKING_FIST));
        bookingServise.findNextBookingToItem(1L);
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findNextBookingToItem(1L, time);
    }

}