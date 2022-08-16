package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingServise;
import ru.practicum.shareit.booking.dto.BookingDtoToCreate;
import ru.practicum.shareit.booking.exceptions.ItemNotAvalibleExxeption;
import ru.practicum.shareit.booking.exceptions.TimeIntersectionException;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDtoWithBoking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiseTest {
    private final ItemServise itemServise;
    private final UserServise userServise;
    private final BookingServise bookingServise;


    @Test
    @DirtiesContext
    void test1_createItem() throws ModelAlreadyExistsException, IncorrectUserIdException, ModelNotExitsException {
        setUser();
        Item item = getItem();
        item.setAvailable(true);
        itemServise.createItem(1, item);

        assertEquals("item1", itemServise.findById(1).getName());
        assertEquals("item1dis, finded", itemServise.findById(1).getDescription());
        assertTrue(itemServise.findById(1).getAvailable());
    }

    private void setUser() throws ModelAlreadyExistsException {
        User user1 = new User(null, "enail@hhh.com", "user1");
        userServise.addUser(user1);
    }

    @Test
    @DirtiesContext
    void test2_CreateItemWithErrors() throws ModelAlreadyExistsException {
        setUser();
        assertThrows(IncorrectUserIdException.class, () -> {
            Item item = getItem();
            itemServise.createItem(2, item);
        });
    }


    @Test
    @DirtiesContext
    void test3_patchItem() throws IncorrectUserIdException, ModelAlreadyExistsException, ModelNotExitsException, IncorectUserOrItemIdException {
        Item item = getItem();
        setUser();
        itemServise.createItem(1, item);

        Item updatedItem = new Item();
        updatedItem.setName("updatedName");
        updatedItem.setAvailable(false);

        itemServise.patchItem(1, 1, updatedItem);

        assertEquals("updatedName", itemServise.findById(1).getName());


        assertEquals(false, itemServise.findById(1).getAvailable());
    }

    @Test
    @DirtiesContext
    void test4_patchWithError() throws ModelAlreadyExistsException, IncorrectUserIdException {
        setUser();
        Item item = getItem();
        User user2 = new User(null, "email2@mail.com", "user2");
        userServise.addUser(user2);
        itemServise.createItem(1, item);

        assertThrows(IncorectUserOrItemIdException.class, () -> {
            Item updatedItem = new Item();
            updatedItem.setAvailable(false);
            itemServise.patchItem(2, 1, updatedItem);
        });
    }


    @Test
    @DirtiesContext
    void test5_findById() throws ModelAlreadyExistsException, IncorrectUserIdException, ModelNotExitsException {
        setUser();
        Item item = getItem();
        itemServise.createItem(1, item);

        assertEquals(item.getName(), itemServise.findById(1).getName());
    }

    @Test
    @DirtiesContext
    void test5_1_findByIdWithBookingAndComment() throws ModelNotExitsException, InterruptedException, NotUsedCommentException {
        data2Users1Item3BookingOwnerUser1();
        sleep(4000);
        itemServise.addComment(1L, 2, "Comment");
        ItemDtoWithBoking itemDtoWithBoking = itemServise.findById(1, 1);
        assertEquals(1, itemDtoWithBoking.getLastBooking().getId());
        assertEquals(2, itemDtoWithBoking.getNextBooking().getId());
        assertTrue(itemDtoWithBoking.getComments().stream().anyMatch(r -> r.getText().equals("Comment")));

    }

    @Test
    @DirtiesContext
    void test6_findNotExitsItem() {
        assertThrows(ModelNotExitsException.class, () -> itemServise.findById(2));
    }

    @Test
    @DirtiesContext
    void test7_findAllByOwnerId() throws ModelAlreadyExistsException, IncorrectUserIdException {
        setUser();
        Item item1 = getItem();
        Item item2 = new Item();
        item2.setAvailable(true);
        item2.setName("item2");
        item2.setDescription("item2");
        User user2 = new User(null, "user2@Email.com", "user2");
        userServise.addUser(user2);
        itemServise.createItem(1, item1);
        itemServise.createItem(2, item2);

        assertTrue(itemServise.findAllByOwnerId(1).stream()
                .map(ItemDtoWithBoking::getName)
                .anyMatch(i -> i.equals(item1.getName())));
    }

    @Test
    @DirtiesContext
    void test8_findItemByText() throws ModelAlreadyExistsException, IncorrectUserIdException {
        setUser();
        Item item = getItem();
        itemServise.createItem(1, item);

        Collection<Item> itemList = itemServise.findByText("finded");
        assertEquals(1, itemList.size());
        assertEquals(itemList.stream().map(Item::getName).findFirst().orElse(null), item.getName());
    }

    @Test
    @DirtiesContext
    void test9_1createComent() throws InterruptedException, ModelNotExitsException, NotUsedCommentException {
        data2Users1Item3BookingOwnerUser1();
        sleep(4000);
        itemServise.addComment(1L, 2, "Comment");
        ItemDtoWithBoking item = itemServise.findById(1, 1);
        assertTrue(item.getComments().stream().anyMatch(r -> r.getText().equals("Comment")));

    }

    @Test
    @DirtiesContext
    void test9_2createComent() {
        data2Users1Item3BookingOwnerUser1();

        assertThrows(NotUsedCommentException.class, () -> itemServise.addComment(1L, 2, "Comment"));
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
        List<BookingDtoToCreate> bList = List.of(
                new BookingDtoToCreate((LocalDateTime.now().plus(Duration.ofSeconds(2))),
                        LocalDateTime.now().plus(Duration.ofSeconds(3)), 1),
                new BookingDtoToCreate((LocalDateTime.now().plus(Duration.ofHours(1))),
                        LocalDateTime.now().plus(Duration.ofHours(2)), 1),
                new BookingDtoToCreate((LocalDateTime.now().plus(Duration.ofHours(2))),
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
                itemServise.createItem(1, r);
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
}
