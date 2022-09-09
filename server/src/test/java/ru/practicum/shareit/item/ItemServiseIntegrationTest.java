package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingServise;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.booking.exceptions.ItemNotAvalibleExxeption;
import ru.practicum.shareit.booking.exceptions.TimeIntersectionException;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;
import ru.practicum.shareit.util.PageParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiseIntegrationTest {
    final ItemDtoMaper itemDtoMaper = new ItemDtoMaper(new BookingDtoMaper());
    private final ItemServise itemServise;
    private final UserServise userServise;
    private final BookingServise bookingServise;

    @Test
    @DirtiesContext
    void test8_findItemByText() throws ModelAlreadyExistsException, IncorrectUserIdException, RequestNotExistException, IncorrectPageValueException {
        setUser();
        Item item = getItem();
        itemServise.createItem(1, itemDtoMaper.toDto(item));
        Collection<Item> itemList = itemServise.findByText("finded", PageParam.createPageable(0, 5));
        assertEquals(1, itemList.size());
        assertEquals(itemList.stream().map(Item::getName).findFirst().orElse(null), item.getName());
    }

    private Item getItem() {
        Item item = new Item();
        item.setName("item1");
        item.setDescription("item1dis, finded");
        item.setAvailable(true);
        return item;
    }

    @SneakyThrows
    private void data2Users1Item3BookingOwnerUser1() {

        List<User> userList = List.of(
                new User(null, "User1@Mail.com", "User1"),
                new User(null, "User2@Mail.com", "User2"));
        List<Item> itemList = List.of(
                new Item(null, "APPROVED", "APPROVED", true, null));
        List<BookItemRequestDto> bList = List.of(
                new BookItemRequestDto((LocalDateTime.now().plus(Duration.ofSeconds(2))),
                        LocalDateTime.now().plus(Duration.ofSeconds(3)), 1),
                new BookItemRequestDto((LocalDateTime.now().plus(Duration.ofHours(1))),
                        LocalDateTime.now().plus(Duration.ofHours(2)), 1),
                new BookItemRequestDto((LocalDateTime.now().plus(Duration.ofHours(2))),
                        LocalDateTime.now().plus(Duration.ofHours(3)), 1));
        userList.forEach(user -> {
            try {
                userServise.addUser(user);
            } catch (ModelAlreadyExistsException e) {
                throw new RuntimeException(e);
            }
        });
        itemList.forEach(r -> {
            try {
                itemServise.createItem(1, itemDtoMaper.toDto(r));
            } catch (IncorrectUserIdException e) {
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
        bookingServise.setStatus(1, 3L, true);

    }

    private void setUser() throws ModelAlreadyExistsException {
        User user1 = new User(null, "enail@hhh.com", "user1");
        userServise.addUser(user1);
    }
}
