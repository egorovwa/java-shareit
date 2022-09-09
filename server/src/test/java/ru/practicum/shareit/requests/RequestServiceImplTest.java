package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.exceptions.UserNotFoundExteption;
import ru.practicum.shareit.exceptions.IncorrectPageValueException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.RequestNotExistException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.impl.RequestServiceImpl;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;
import ru.practicum.shareit.util.PageParam;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    UserServise mokUserService;
    @Mock
    RequestRepository mokRequestRepository;
    @InjectMocks
    RequestServiceImpl requestService;


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
        assertThrows(ModelNotExitsException.class, () -> requestService.createRequest(2L, itemRequestDto),
                "Создается от несуществующего user");
    }

    @Test
    void test2_findAll_withPage() throws ModelNotExitsException, IncorrectPageValueException {

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

        Collection<ItemRequest> withPageble =
                requestService.findAllWithPage(PageParam.createPageable(0, 2, "created"), 1L);
        Collection<ItemRequest> withOutPageble = requestService.findAllWithPage(null, 1L);
        assertThat("from, size", withPageble.stream().findFirst().get().getDescription(),
                is(testItemRequest(TEST_TIME_LONG).getDescription()));
        assertThat(withOutPageble.stream().findFirst().get().getDescription(),
                is(testItemRequest2(TEST_TIME_LONG).getDescription()));
    }

    @Test
    void test2_3findAll_withOutPage() throws ModelNotExitsException {
        List<ItemRequest> itemRequests = List.of(testItemRequest(TEST_TIME_LONG),
                testItemRequest(TEST_TIME_LONG + 10000));
        Mockito
                .when(mokRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(1L))
                .thenReturn(itemRequests);
        Mockito
                .when(mokRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(1L))
                .thenReturn(List.of(testItemRequest2(TEST_TIME_LONG), testItemRequest(TEST_TIME_LONG + 10000)));
        Mockito
                .when(mokUserService.findById(Mockito.anyLong()))
                .thenReturn(USER_ID2);
        RequestServiceImpl requestService = new RequestServiceImpl(mokUserService, mokRequestRepository);
        Collection<ItemRequest> withOutPageble = requestService.findAllWithPage(null, 1L);
        assertThat(withOutPageble.stream().findFirst().get().getDescription(),
                is(testItemRequest2(TEST_TIME_LONG).getDescription()));
    }

    @Test
    void test2_2findAll_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(mokUserService.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(UserNotFoundExteption.class, () ->
                requestService.findAllWithPage(PageParam.createPageable(0, 5), 1L));
    }

    @Test
    void test3_findById_notFound() {
        Mockito
                .when(mokRequestRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(RequestNotExistException.class, () -> requestService.findById(1L));
    }

    @Test
    void test3_findById() throws RequestNotExistException {
        Mockito
                .when(mokRequestRepository.findById(1L))
                .thenReturn(Optional.of(testItemRequest(TEST_TIME_LONG)));
        assertThat(requestService.findById(1L), is(testItemRequest(TEST_TIME_LONG)));
    }

    @Test
    void test4_findAllForRequestor_requestorNotFound() throws ModelNotExitsException {
        Mockito
                .when(mokUserService.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(ModelNotExitsException.class, () -> requestService.findAllForRequestor(1L));
    }

    @Test
    void test4_1_findAllForRequestor() throws ModelNotExitsException {
        Mockito
                .when(mokUserService.findById(1L))
                .thenReturn(USER_ID1);
        requestService.findAllForRequestor(1L);
        Mockito.verify(mokRequestRepository, Mockito.times(1))
                .findItemRequestsByRequestorIdOrderByCreatedDesc(1L);
    }

    @Test
    void test5_findItemRequest_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(mokUserService.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(ModelNotExitsException.class, () -> requestService.findItemRequest(1L, 1L));
    }

    @Test
    void test5_1_findItemRequest_requesNotFound() {
        Mockito
                .when(mokRequestRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotExitsException.class, () -> requestService.findItemRequest(1L, 1L));
    }

    @Test
    void test5_2_findItemRequest() throws ModelNotExitsException {
        Mockito
                .when(mokUserService.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(mokRequestRepository.findById(1L))
                .thenReturn(Optional.of(testItemRequest(TEST_TIME_LONG)));
        assertThat(requestService.findItemRequest(1L, 1L), is(testItemRequest(TEST_TIME_LONG)));
    }

    @Test
    void test6_save() {
        ItemRequest itemRequest = ITEMREQUEST_ID1_USER1;
        Mockito
                .when(mokRequestRepository.save(itemRequest))
                .thenReturn(itemRequest);
        assertThat(requestService.save(itemRequest), is(itemRequest));
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
