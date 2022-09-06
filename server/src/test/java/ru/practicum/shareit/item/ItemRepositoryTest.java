package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.TestConstants.USER_ID1;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Test
    @DirtiesContext
    void test1_1findByText_whenTextInName_From0_size3() {
        userRepository.save(USER_ID1);
        Item item1 = new Item(null, "item 1", "did", true, USER_ID1);
        Item item2 = new Item(null, "item 2", "dd", true, USER_ID1);
        Item item3 = new Item(null, "item 3", "dddd", true, USER_ID1);
        Item item4 = new Item(null, "item 4", "ddddd", true, USER_ID1);
        Item item5 = new Item(null, "item 5", "dddd", true, USER_ID1);
        Item item6 = new Item(null, "item 6", "dddd", true, USER_ID1);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRepository.save(item4);
        itemRepository.save(item5);
        itemRepository.save(item6);
        Pageable pageable = PageRequest.of(0, 3);
        List<Item> itemList1 = itemRepository.findByText(pageable, "item").toList();

        assertThat(itemList1.size(), is(3));

        Pageable pageable2 = PageRequest.of(1, 3);
        List<Item> itemList2 = itemRepository.findByText(pageable2, "item").toList();
        assertThat(itemList2.size(), is(3));

        List<Item> allItemIn = List.of(item1, item2, item3, item4, item5, item6);
        List<Item> allItemOut = new ArrayList<>(itemList1);
        allItemOut.addAll(itemList2);
        assertTrue(allItemIn.containsAll(allItemIn));
    }

    @Test
    @DirtiesContext
    void test1_2findByText_3_withOutPage() {
        userRepository.save(USER_ID1);
        Item item1 = new Item(null, "item 1", "did", true, USER_ID1);
        Item item2 = new Item(null, "item 2", "dd", true, USER_ID1);
        Item item3 = new Item(null, "item 3", "dddd", true, USER_ID1);
        Item item4 = new Item(null, "item 4", "ddddd", true, USER_ID1);
        Item item5 = new Item(null, "item 5", "dddd", true, USER_ID1);
        Item item6 = new Item(null, "item 6", "dddd", true, USER_ID1);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRepository.save(item4);
        itemRepository.save(item5);
        itemRepository.save(item6);
        Item itemfinded = new Item(null, "item 3", "dddd", true, USER_ID1);
        itemfinded.setId(3L);

        Collection<Item> itemList = itemRepository.findByText("3");
        assertThat(itemList.stream().findFirst().get(), is(itemfinded));
        assertThat(itemList.size(), is(1));
    }

    @Test
    @DirtiesContext
    void test1_3findByText_inDescription() {
        userRepository.save(USER_ID1);

        Item item1 = new Item(null, "item 1", "did", true, USER_ID1);
        Item item2 = new Item(null, "item 2", "dd", true, USER_ID1);
        Item item3 = new Item(null, "item 3", "dddd", true, USER_ID1);
        Item item4 = new Item(null, "item 4", "ddd55dd", true, USER_ID1);
        Item item5 = new Item(null, "item 5", "dddd", true, USER_ID1);
        Item item6 = new Item(null, "item 6", "dddd", true, USER_ID1);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRepository.save(item4);
        itemRepository.save(item5);
        itemRepository.save(item6);
        Collection<Item> itemList = itemRepository.findByText("dddd");
        assertThat(itemList.size(), is(3));
    }
}