package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingServise;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.exceptions.ItemNotAvalibleExxeption;
import ru.practicum.shareit.booking.exceptions.ParametrNotFoundException;
import ru.practicum.shareit.booking.exceptions.StatusAlredyException;
import ru.practicum.shareit.booking.exceptions.TimeIntersectionException;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiseImplTest {
    private final BookingServise bookingServise;
    private final ItemServise itemServise;
    private final UserServise userServise;

    @Test
    @DirtiesContext
    void test1_createBooking_normal() throws ModelAlreadyExistsException, IncorrectUserIdException,
            ModelNotExitsException, ItemNotAvalibleExxeption, TimeIntersectionException {
        data2User2Item();
        BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now().plus(Duration.ofMinutes(1)),
                LocalDateTime.now().plus(Duration.ofHours(1)), 1);
        Booking booking = bookingServise.createBooking(bookingToCreate, 2);

        assertEquals(1, booking.getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(userServise.findById(2), booking.getBooker());
    }

    @Test
    @DirtiesContext
    void test1_2errors_BookingCreateTimeException() throws ModelAlreadyExistsException, IncorrectUserIdException, RequestNotExistException {
        data2User2Item();
        assertThrows(TimeIntersectionException.class, () -> {
            BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now().plus(Duration.ofHours(1)),
                    LocalDateTime.now(), 1);
            bookingServise.createBooking(bookingToCreate, 2);
        });

    }

    @Test
    @DirtiesContext
    void test1_3_errors_BookingCreateUserException() throws ModelAlreadyExistsException, IncorrectUserIdException, RequestNotExistException {
        data2User2Item();
        assertThrows(ModelNotExitsException.class, () -> {
            BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now(),
                    LocalDateTime.now().plus(Duration.ofHours(1)), 1);
            bookingServise.createBooking(bookingToCreate, 3);
        });

    }

    @Test
    @DirtiesContext
    void test1_4_errors_BookingCreateOwnerUserException() throws ModelAlreadyExistsException, IncorrectUserIdException, RequestNotExistException {
        data2User2Item();
        assertThrows(ModelNotExitsException.class, () -> {
            BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now(),
                    LocalDateTime.now().plus(Duration.ofHours(1)), 1);
            bookingServise.createBooking(bookingToCreate, 1);
        });

    }


    @Test
    @DirtiesContext
    void test1_5errors_BookingCreateItemException() throws ModelAlreadyExistsException, IncorrectUserIdException, RequestNotExistException {
        data2User2Item();
        assertThrows(ModelNotExitsException.class, () -> {
            BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now(),
                    LocalDateTime.now().plus(Duration.ofHours(1)), 3);
            bookingServise.createBooking(bookingToCreate, 3);
        });
    }

    @Test
    @DirtiesContext
    void test1_6errors_BookingCreateItemNotAvailableException() throws ModelAlreadyExistsException, IncorrectUserIdException, ModelNotExitsException, IncorectUserOrItemIdException {
        data2User2Item();
        Item update = new Item(null, null, null, false, null);
        itemServise.patchItem(1, 1, update);
        assertThrows(ItemNotAvalibleExxeption.class, () -> {
            BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now(),
                    LocalDateTime.now().plus(Duration.ofHours(1)), 1);
            bookingServise.createBooking(bookingToCreate, 2);
        });
    }

    @Test
    @DirtiesContext
    void test2_1_setStatus() throws ModelAlreadyExistsException, IncorrectUserIdException, ModelNotExitsException, ItemNotAvalibleExxeption, TimeIntersectionException, ParametrNotFoundException {
        data2User2Item();
        dataUser1CreateBookingItem2();
        bookingServise.setStatus(2, 1L, true);
        assertEquals(BookingStatus.APPROVED, bookingServise.findById(1, 2).getStatus());
    }

    @Test
    @DirtiesContext
    void test2_2_setStatusWhenStatusNotWaiting() throws ModelAlreadyExistsException, IncorrectUserIdException, ModelNotExitsException, ItemNotAvalibleExxeption, TimeIntersectionException, ParametrNotFoundException {
        data2User2Item();
        dataUser1CreateBookingItem2();
        bookingServise.setStatus(2, 1L, true);

        assertThrows(StatusAlredyException.class, () -> bookingServise.setStatus(2, 1L, true));

    }

    @Test
    @DirtiesContext
    void test2_3_setStatusWhenNotOwner() throws ModelAlreadyExistsException, IncorrectUserIdException, ModelNotExitsException, ItemNotAvalibleExxeption, TimeIntersectionException {
        data2User2Item();
        dataUser1CreateBookingItem2();

        assertThrows(IncorrectUserIdException.class, () -> bookingServise.setStatus(1, 1L, true));

    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test3_1_findById() {
        data2User2Item();
        dataUser1CreateBookingItem2();
        Booking booking = bookingServise.findById(1, 2);

        assertEquals(2, booking.getItem().getId());
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test3_2_finndByIdAnotherUser() {
        data2User2Item();
        dataUser1CreateBookingItem2();
        userServise.addUser(new User(null, "aaa@Email.com", "another"));
        assertThrows(IncorrectUserIdException.class, () -> {
            Booking booking = bookingServise.findById(1, 3);
        });
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test4_1getAllUserWithOutState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllUser(2);

        assertEquals(3, bookingServise.getAllUser(2).size());

    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test4_2getAllUserWithAllState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllUser(2, BookingState.ALL);

        assertEquals(3, bookingServise.getAllUser(2).size());

    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test4_3getAllUserWithWaitingState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllUser(2, BookingState.WAITING);
        assertEquals(1, findedList.size());
        assertEquals("WAITING", findedList.stream().findFirst().get().getItem().getName());
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test4_4getAllUserWithRejectedState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllUser(2, BookingState.REJECTED);
        assertEquals(1, findedList.size());
        assertEquals("REJECTED", findedList.stream().findFirst().get().getItem().getName());
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test4_5getAllUserWithFutureState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllUser(2, BookingState.FUTURE);
        assertEquals(3, findedList.size());
        assertTrue(findedList.stream().anyMatch(r -> r.getItem().getName()
                .equals("APPROVED")));
        assertTrue(findedList.stream().anyMatch(r -> r.getItem().getName()
                .equals("REJECTED")));
        assertTrue(findedList.stream().anyMatch(r -> r.getItem().getName()
                .equals("WAITING")));

    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test4_6getAllUserWithWaitingState() {
        data2Users3Item3BookingOwnerUser1();
        sleep(2000);
        Collection<Booking> findedList = bookingServise.getAllUser(2, BookingState.CURRENT);
        assertEquals(1, findedList.size());
        assertEquals("APPROVED", findedList.stream().findFirst().get().getItem().getName());
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test5_1getAllOwnerWithOutState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllOwner(1);

        assertEquals(3, bookingServise.getAllUser(2).size());

    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test5_2getAllOwnerWithAllState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllOwner(1, BookingState.ALL);

        assertEquals(3, bookingServise.getAllUser(2).size());

    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test5_3getAllOwnerWithWaitingState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllOwner(1, BookingState.WAITING);
        assertEquals(1, findedList.size());
        assertEquals("WAITING", findedList.stream().findFirst().get().getItem().getName());
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test5_4getAllOwnerWithRejectedState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllOwner(1, BookingState.REJECTED);
        assertEquals(1, findedList.size());
        assertEquals("REJECTED", findedList.stream().findFirst().get().getItem().getName());
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test5_5getAllOwnerWithFutureState() {
        data2Users3Item3BookingOwnerUser1();
        Collection<Booking> findedList = bookingServise.getAllOwner(1, BookingState.FUTURE);
        assertEquals(3, findedList.size());
        assertTrue(findedList.stream().anyMatch(r -> r.getItem().getName()
                .equals("APPROVED")));
        assertTrue(findedList.stream().anyMatch(r -> r.getItem().getName()
                .equals("REJECTED")));
        assertTrue(findedList.stream().anyMatch(r -> r.getItem().getName()
                .equals("WAITING")));

    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test5_6getAllOwnerWithWaitingState() {
        data2Users3Item3BookingOwnerUser1();
        sleep(2000);
        Collection<Booking> findedList = bookingServise.getAllOwner(1, BookingState.CURRENT);
        assertEquals(1, findedList.size());
        assertEquals("APPROVED", findedList.stream().findFirst().get().getItem().getName());
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test6_1findNextBookingToItem() {
        data2Users3Item3BookingOwnerUser1();
        sleep(2000);
        Optional<Booking> booking = bookingServise.findNextBookingToItem(2);
        assertEquals(2, booking.get().getId());
    }

    @Test
    @DirtiesContext
    @SneakyThrows
    void test7_1findLastBookingToItem() {
        data2Users3Item3BookingOwnerUser1();
        sleep(5000);
        Optional<Booking> booking = bookingServise.findNextBookingToItem(2);
        assertEquals(2, booking.get().getId());
    }

    private void data2User2Item() throws ModelAlreadyExistsException, IncorrectUserIdException, RequestNotExistException {
        User user1 = new User(null, "User1@Mail.com", "User1");
        User user2 = new User(null, "User2@Mail.com", "User2");
        ItemDto item1 = new ItemDto(null, "item1", "user1 Item1", true, null,null);
        ItemDto item2 = new ItemDto(null, "item2", "user2 Item2", true, null,null);
        userServise.addUser(user1);
        userServise.addUser(user2);
        itemServise.createItem(1, item1);
        itemServise.createItem(2, item2);
    }

    private void dataUser1CreateBookingItem2() throws ModelNotExitsException, ItemNotAvalibleExxeption, TimeIntersectionException {
        BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now().plus(Duration.ofMinutes(1)),
                LocalDateTime.now().plus(Duration.ofHours(1)), 2);
        Booking booking = bookingServise.createBooking(bookingToCreate, 1);

    }

    @SneakyThrows
    private void data2Users3Item3BookingOwnerUser1() {
        List<User> userList = List.of(
                new User(null, "User1@Mail.com", "User1"),
                new User(null, "User2@Mail.com", "User2"));
        List<ItemDto> itemList = List.of(
                new ItemDto(null, "APPROVED", "APPROVED", true, null, null),
                new ItemDto(null, "WAITING", "WAITING", true, null, null),
                new ItemDto(null, "REJECTED", "REJECTED", true, null, null));
        List<BookingDtoToCreate> bList = List.of(
                new BookingDtoToCreate((LocalDateTime.now().plus(Duration.ofSeconds(2))),
                        LocalDateTime.now().plus(Duration.ofHours(1)), 1),
                new BookingDtoToCreate((LocalDateTime.now().plus(Duration.ofHours(1))),
                        LocalDateTime.now().plus(Duration.ofHours(2)), 2),
                new BookingDtoToCreate((LocalDateTime.now().plus(Duration.ofHours(2))),
                        LocalDateTime.now().plus(Duration.ofHours(3)), 3));
        userList.forEach(user -> {
            try {
                userServise.addUser(user);
            } catch (ModelAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        });
        itemList.forEach(r -> {
            try {
                itemServise.createItem(1, r);
            } catch (IncorrectUserIdException | RequestNotExistException e) {
                throw new RuntimeException(e);
            }
        });
        bList.forEach(r -> {
            try {
                bookingServise.createBooking(r, 2);
            } catch (TimeIntersectionException | ItemNotAvalibleExxeption | ModelNotExitsException e) {
                throw new RuntimeException(e);
            }
        });
        bookingServise.setStatus(1, 1L, true);
        bookingServise.setStatus(1, 3L, false);
    }
}