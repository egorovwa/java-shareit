package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {
    private final UserServise userServise;
    private final ItemServise itemServise;
    private User user1 = new User(null, "user1@nfff.com", "user1");
    Item item = new Item(null, "name rrrr", "word finded word", true, user1);
    private final User user2 = new User(null, "user2@nfff.com", "user2");

    @Test
    void contextLoads() {
    }
}


