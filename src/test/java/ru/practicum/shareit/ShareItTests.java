package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {
    private final UserServise userServise;
    private final ItemServise itemServise;
    private User user1 = new User(null, "user1@nfff.com", "user1");
    private User user2 = new User(null, "user2@nfff.com", "user2");
    Item item = new Item(null, "name rrrr", "word finded word", true, user1);

    @Test
    void contextLoads() {
    }

    @Test
    void test1_findItemByText() throws ModelAlreadyExistsException, IncorrectUserIdException {
        user1 = userServise.addUser(user1);
        itemServise.createItem(user1.getId(), item);

        Collection<Item> itemList = itemServise.findByText("finded");
        assertEquals(1, itemList.size());
        assertEquals(itemList.stream().map(Item::getName).findFirst().orElse(null), item.getName());
    }
}


