package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static ru.practicum.shareit.Entitys.*;

@DataJpaTest
class BookingRepositoryTest {
    private static long HOUR = 1000 * 60 * 60;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;

    User user1;
    User user2;
    User user3;

    Item item1u1;
    Item item2u1;
    Item item3u1;
    Item item4u2;
    Item item5u2;
    Item item6u2;
    Booking bookingCurrent1;
    Booking bookingPast2;
    Booking bookingFuture3;
    Booking bookingWaiting4;

    private void data() {
        user1 = userRepository.save(USER_ID1);
        user2 = userRepository.save(USER_ID2);
        user3 = userRepository.save(USER_ID3);

        item1u1 = itemRepository.save(new Item(1L, "item 1", "did", true, USER_ID1));
        item2u1 = itemRepository.save(new Item(2L, "item 2", "dd", true, USER_ID1));
        item3u1 = itemRepository.save(new Item(3L, "item 3", "dddd", true, USER_ID1));
        item4u2 = itemRepository.save(new Item(4L, "item 4", "ddd55dd", true, USER_ID2));
        item5u2 = itemRepository.save(new Item(5L, "item 5", "dddd", true, USER_ID2));
        item6u2 = itemRepository.save(new Item(6L, "item 6", "dddd", true, USER_ID2));

        bookingCurrent1 = bookingRepository.save(new Booking(null,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - HOUR,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + 10000, item1u1, user2, BookingStatus.APPROVED));
        bookingPast2 = bookingRepository.save(new Booking(null,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - HOUR,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - 10000, item1u1, user2, BookingStatus.APPROVED));
        bookingFuture3 = bookingRepository.save(new Booking(null,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + 10000,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + HOUR, item1u1, user2, BookingStatus.APPROVED));
        bookingWaiting4 = bookingRepository.save(new Booking(null,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - HOUR * 2,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - HOUR, item1u1, user2, BookingStatus.WAITING));
    }

    @Test
    @DirtiesContext
    void findByBooker_IdOrderByStartDesc() {
        data();
        Pageable pageable = PageRequest.of(0, 5);
        List<Booking> bookingList = bookingRepository.findByBooker_IdOrderByStartDesc(pageable, 2L).toList();
        assertThat(bookingList.size(), is(4));
        assertThat(bookingList.stream().findFirst().get(), is(bookingFuture3));

    }

    @Test
    @DirtiesContext
    void findByBookerIdStatePast() {
        data();
        Collection<Booking> bookingList = bookingRepository.findByBookerIdStatePast(2L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(bookingList.stream().findFirst().get(), is(bookingPast2));
    }

    @Test
    @DirtiesContext
    void findByBooker_IdAndStatus_WAITING() {
        data();
        Collection<Booking> bookings = bookingRepository.findByBooker_IdAndStatus(2L, BookingStatus.WAITING);
        assertThat(bookings.stream().findFirst().get(), is(bookingWaiting4));
    }

    @Test
    @DirtiesContext
    void findByBookerIdStateCurrent() {
        data();
        Collection<Booking> bookings = bookingRepository.findByBookerIdStateCurrent(2L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(bookings.stream().findFirst().get(), is(bookingCurrent1));
    }

    @Test
    @DirtiesContext
    void findFuture() {
        data();
        Collection<Booking> bookings = bookingRepository.findFuture(2L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(bookings.stream().findFirst().get(), is(bookingFuture3));
    }

    @Test
    @DirtiesContext
    void findOwnerAll() {
        data();
        Pageable pageable = PageRequest.of(0, 5);
        Collection<Booking> bookingList = bookingRepository.findOwnerAll(1L);
        assertThat(bookingList.size(), is(4));

    }


    @Test
    @DirtiesContext
    void findOwnerFuture() {
        data();
        Collection<Booking> bookings = bookingRepository.findOwnerFuture(1L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(bookings.stream().findFirst().get(), is(bookingFuture3));
    }

    @Test
    @DirtiesContext
    void findOwnerCurrent() {
        data();
        Collection<Booking> bookings = bookingRepository.findOwnerCurrent(1L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(bookings.stream().findFirst().get(), is(bookingCurrent1));
    }

    @Test
    @DirtiesContext
    void findByOwnerIdAndStatus() {
        data();
        Collection<Booking> bookings = bookingRepository.findByOwnerIdAndStatus(1L, BookingStatus.WAITING);
        assertThat(bookings.stream().findFirst().get(), is(bookingWaiting4));
    }

    @Test
    @DirtiesContext
    void findOwnerPast() {
        data();
        Collection<Booking> bookingList = bookingRepository.findOwnerPast(1L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(bookingList.stream().findFirst().get(), is(bookingPast2));
    }

    @Test
    @DirtiesContext
    void findLastBookingToItem() {
        data();
        Collection<Booking> bookings = bookingRepository.findLastBookingToItem(1L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(bookings.stream().findFirst().get(), is(bookingPast2));
    }

    @Test
    @DirtiesContext
    void findNextBookingToItem() {
        data();
        Collection<Booking> bookings = bookingRepository.findNextBookingToItem(1L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(bookings.stream().findFirst().get(), is(bookingFuture3));
    }

    @Test
    @DirtiesContext
    void usedCount() {
        data();
        Integer count = bookingRepository.usedCount(2L,1L,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        assertThat(count, is(1));
    }
}