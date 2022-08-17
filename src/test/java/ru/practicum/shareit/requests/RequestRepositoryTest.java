package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.Entitys.*;

@DataJpaTest
class RequestRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    private RequestRepository requestRepository;

    @Test
    void test1_createRequest() {
        User user = userRepository.save(USER_ID1);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("escription");
        itemRequest.setCreated(TEST_TIME_LONG);
        itemRequest.setRequestor(user);
        requestRepository.save(itemRequest);
        ItemRequest savedRequest = testEntityManager.find(ItemRequest.class, 1L);
        assertTrue(savedRequest != null);
        assertThat(savedRequest.getDescription(), is(itemRequest.getDescription()));
        assertThat(savedRequest.getCreated(), is(TEST_TIME_LONG));
        assertThat(savedRequest.getRequestor(), is(USER_ID1));

    }
    @Test
    void test2_createRequestWithOutDescription(){
        User user = userRepository.save(USER_ID1);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setCreated(TEST_TIME_LONG);
        itemRequest.setRequestor(user);
        assertThrows(DataIntegrityViolationException.class,()->requestRepository.save(itemRequest));
    }
    @Test
    void test3_findAllWithPage() throws InterruptedException {
        User userRequestor = userRepository.save(USER_ID1);
        User userResponser = userRepository.save(USER_ID2);
        String  description = "Created : ";
        List<ItemRequest> savedRequests = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ItemRequest itemRequest = new ItemRequest();
            Long createTime = TEST_TIME_DATE_TIME.minus(Duration.ofHours(i))
                    .toEpochSecond(ZoneOffset.UTC);
            itemRequest.setCreated(createTime);
            itemRequest.setDescription(description + LocalDateTime
                    .ofEpochSecond(createTime,0,ZoneOffset.UTC));
            itemRequest.setRequestor(userRequestor);
            savedRequests.add(requestRepository.save(itemRequest));
            ;
        }

        List<ItemRequest> requestList = requestRepository.findAll(PageRequest.of(0,3,
                Sort.by("created").ascending())).toList();
        System.out.println(requestList);
        assertThat(requestList.size(), is(3));
        assertThat(requestList.get(0), is(savedRequests.get(4)));
        assertThat(requestList.get(2), is(savedRequests.get(2)));
    }

}