package ru.practicum.shareit;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

public class TestConstants {
    public static final User USER_ID1 = new User(1L, "Mail@mail.ru", "userName");
    public static final User USER_ID2 = new User(2L, "Mail2@mail.ru", "userName2");
    public static final User USER_ID3 = new User(3L, "Mail3@mail.ru", "userName3");
    public static final LocalDateTime TEST_TIME_DATE_TIME = LocalDateTime.of(2022, 8, 17,
            6, 0, 0);
    public static final Long TEST_TIME_LONG = TEST_TIME_DATE_TIME.toEpochSecond(ZoneOffset.UTC);
    public static final ItemRequest ITEMREQUEST_ID1_USER1 = new ItemRequest(1L, "itemrequest 1",
            USER_ID1, TEST_TIME_LONG, new ArrayList<>());
    public static final Item ITEM_ID1_OWNER1_AVALIBLE_TRUE = new Item(1L, "name", "d", true, USER_ID1);
    public static final Booking BOOKING_FIST = new Booking(1L, TEST_TIME_LONG, TEST_TIME_LONG + 10000,
            ITEM_ID1_OWNER1_AVALIBLE_TRUE, USER_ID1, BookingStatus.WAITING);
    public static final Booking BOOKING_NEXT = new Booking(1L, TEST_TIME_LONG + 20000,
            TEST_TIME_LONG + 40000, ITEM_ID1_OWNER1_AVALIBLE_TRUE, USER_ID1, BookingStatus.WAITING);
    public static final Comment COMMENTID1_USER2 = new Comment(1L, "comment1", ITEM_ID1_OWNER1_AVALIBLE_TRUE, USER_ID2,
            TEST_TIME_LONG + 1000);
    public static final Booking BOOKING1_USER2_ITEM1_WAITING = new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
            LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC), ITEM_ID1_OWNER1_AVALIBLE_TRUE,
            USER_ID2, BookingStatus.WAITING);
    public static final Booking BOOKING1_USER2_ITEM1_APPROVED = new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
            LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC), ITEM_ID1_OWNER1_AVALIBLE_TRUE,
            USER_ID2, BookingStatus.APPROVED);
    public static final Booking BOOKING1_USER2_ITEM1_REJECTED = new Booking(1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
            LocalDateTime.now().plus(Duration.ofHours(1)).toEpochSecond(ZoneOffset.UTC), ITEM_ID1_OWNER1_AVALIBLE_TRUE,
            USER_ID2, BookingStatus.REJECTED);
    public static final BookingDtoToCreate BOOKING_DTO_TO_CREATE_ITEM1 = new BookingDtoToCreate(TEST_TIME_DATE_TIME,
            TEST_TIME_DATE_TIME.plus(Duration.ofHours(1)), 1L);


}
