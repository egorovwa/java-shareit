package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiseTest {
    private final ItemServise itemServise;
    private final UserServise userServise;


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
        assertThrows(IncorrectUserIdException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Item item = getItem();
                itemServise.createItem(2, item);
            }
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
        User user2 = new User(null, "user2", "email2@www.com");
        userServise.addUser(user2);
        itemServise.createItem(1, item);

        assertThrows(IncorectUserOrItemIdException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                Item updatedItem = new Item();
                updatedItem.setAvailable(false);
                itemServise.patchItem(2, 1, updatedItem);
            }
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
    void test6_findNotExitsItem() {
        assertThrows(ModelNotExitsException.class, new Executable() {
            @Override
            public void execute() throws Throwable {
                itemServise.findById(2);
            }
        });
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
                .map(Item::getName)
                .anyMatch(i -> i.equals(item1.getName())));
    }

    @Test
    @DirtiesContext
    void test1_findItemByText() throws ModelAlreadyExistsException, IncorrectUserIdException {
        setUser();
        Item item = getItem();
        itemServise.createItem(1, item);

        Collection<Item> itemList = itemServise.findByText("finded");
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
}