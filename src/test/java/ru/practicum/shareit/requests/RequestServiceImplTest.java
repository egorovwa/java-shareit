package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.impl.RequestServiceImpl;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static ru.practicum.shareit.Entitys.TEST_TIME_LONG;
import static ru.practicum.shareit.Entitys.USER_ID2;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    UserServise mokUserService;
    @Mock
    RequestRepository mokRequestRepository;


    @Test
    void test1_createItemRequest() throws ModelNotExitsException {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Long createTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        itemRequestDto.setDescription("Request 1");
        Mockito
                .when(mokUserService.findById(1))
                .thenReturn(testUser());
        Mockito
                .when(mokRequestRepository.save(Mockito.any()))
                .thenReturn(testItemRequest(createTime));
        Mockito
                .when(mokUserService.findById(2))
                .thenThrow(ModelNotExitsException.class);
        RequestServiceImpl requestService = new RequestServiceImpl(mokUserService, mokRequestRepository);
        requestService.createRequest(1L, itemRequestDto);
        ItemRequest itemRequest = requestService.createRequest(1L, itemRequestDto);

        assertThat("Description не совпадает", itemRequestDto.getDescription(), is(itemRequest.getDescription()));
        assertThat("createTime не совпадает", itemRequest.getCreated(), is(createTime));
        assertThat("user не совпадает", itemRequest.getRequestor().getEmail(), is(testUser().getEmail()));
        Assertions.assertThrows(ModelNotExitsException.class, () -> requestService.createRequest(2L, itemRequestDto),
                "Создается от несуществующего user");
    }

    @Test
    void test2_findAll() throws ModelNotExitsException {

        Pageable pageable = PageRequest.of(0, 2, Sort.by("created").descending());
        List<ItemRequest> itemRequests = List.of(testItemRequest(TEST_TIME_LONG),
                testItemRequest(TEST_TIME_LONG + 10000));
        Page<ItemRequest> requestPage = new PageImpl<>(itemRequests, pageable, 2);


        Mockito
                .when(mokRequestRepository.findAllByRequestorIdIsNot(pageable, 1L))
                .thenReturn(requestPage);
        Mockito
                .when(mokRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(1L))
                .thenReturn(List.of(testItemRequest2(TEST_TIME_LONG), testItemRequest(TEST_TIME_LONG + 10000)));
        Mockito
                .when(mokUserService.findById(Mockito.anyLong()))
                .thenReturn(USER_ID2);
        RequestServiceImpl requestService = new RequestServiceImpl(mokUserService, mokRequestRepository);


        Collection<ItemRequest> withPageble = requestService.findAllWithPage(0, 2, 1L);
        Collection<ItemRequest> withOutPageble = requestService.findAllWithPage(null, null, 1L);
        assertThat("from, size", withPageble.stream().findFirst().get().getDescription(),
                is(testItemRequest(TEST_TIME_LONG).getDescription()));
        assertThat(withOutPageble.stream().findFirst().get().getDescription(),
                is(testItemRequest2(TEST_TIME_LONG).getDescription()));
    }

    private User testUser() {
        return new User(1L, "Email@mail.com", "user");
    }

    private ItemRequest testItemRequest(Long timeCreated) {
        return new ItemRequest(1L, "Request 1", testUser(), timeCreated, null);
    }

    private ItemRequest testItemRequest2(Long timeCreated) {
        return new ItemRequest(1L, "Request 2", testUser(), timeCreated, null);
    }
}
