package ru.practicum.shareit.booking.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingServise;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.booking.exceptions.ItemNotAvalibleExxeption;
import ru.practicum.shareit.booking.exceptions.TimeIntersectionException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiseImplTest {
    private final BookingServise bookingServise;
    private final ItemServise itemServise;
    private final UserServise userServise;
    private Long timeNow = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    private final long DAY = 60 * 60 * 24;
    private final long HOUR = 60 * 60;

    @Test
    @DirtiesContext
    void test1_createBooking_normal() throws ModelAlreadyExistsException, IncorrectUserIdException,
            ModelNotExitsException, ItemNotAvalibleExxeption, TimeIntersectionException {
        data2User2Item();
        BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now(),
                LocalDateTime.now().plus(Duration.ofHours(1)), 1);
        Booking booking = bookingServise.createBooking(bookingToCreate, 2);

        assertEquals(1, booking.getId());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
        assertEquals(userServise.findById(2), booking.getBooker());
    }

    @Test
    @DirtiesContext
    void test2_errors_BookingCreateTimeException() throws ModelAlreadyExistsException, IncorrectUserIdException {
        data2User2Item();
        assertThrows(TimeIntersectionException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now().plus(Duration.ofHours(1)),
                        LocalDateTime.now(), 1);
                bookingServise.createBooking(bookingToCreate, 2);
            }
        });

    }

    @Test
    @DirtiesContext
    void test2_errors_BookingCreateUserException() throws ModelAlreadyExistsException, IncorrectUserIdException {
        data2User2Item();
        assertThrows(ModelNotExitsException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now(),
                        LocalDateTime.now().plus(Duration.ofHours(1)), 1);
                bookingServise.createBooking(bookingToCreate, 3);
            }
        });

    }

    @Test
    @DirtiesContext
    void test2_errors_BookingCreateItemException() throws ModelAlreadyExistsException, IncorrectUserIdException {
        data2User2Item();
        assertThrows(ModelNotExitsException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                BookingDtoToCreate bookingToCreate = new BookingDtoToCreate(LocalDateTime.now(),
                        LocalDateTime.now().plus(Duration.ofHours(1)), 3);
                bookingServise.createBooking(bookingToCreate, 3);
            }
        });
    }

    @Test
    void setStatus() {
    }

    private void data2User2Item() throws ModelAlreadyExistsException, IncorrectUserIdException {
        User user1 = new User(null, "User1@Mail.com", "User1");
        User user2 = new User(null, "User2@Mail.com", "User2");
        Item item1 = new Item(null, "item1", "user1 Item1", true, null);
        Item item2 = new Item(null, "item2", "user2 Item2", true, null);
        userServise.addUser(user1);
        userServise.addUser(user2);
        itemServise.createItem(1, item1);
        itemServise.createItem(2, item2);
    }
}