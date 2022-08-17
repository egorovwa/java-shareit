package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoMaper;
import ru.practicum.shareit.requests.impl.RequestServiceImpl;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    UserServise mokUserService;
    @Mock
    ItemRequestDtoMaper maper = new ItemRequestDtoMaper();
    @Mock
    RequestRepository mokRequestRepository;


    @Test
    void test1_createItemRequest() throws ModelNotExitsException {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        Long createTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        itemRequestDto.setDescription("Description, ffffff");
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
        assertThat("Description не совпадает",itemRequestDto.getDescription(), is(itemRequest.getDescription()));
        assertThat("createTime не совпадает",itemRequest.getCreated(), is(createTime));
        assertThat("user не совпадает",itemRequest.getRequestor().getEmail(), is(testUser().getEmail()));
        Assertions.assertThrows(ModelNotExitsException.class,()->requestService.createRequest(2L,itemRequestDto),
                "Создается от несуществующего user");
    }

    private User testUser() {
        return new User(1L, "Email@mail.com", "user");
    }

    private ItemRequest testItemRequest(Long timeCreated) {
        return new ItemRequest(1L, "Description, ffffff", testUser(), timeCreated,null);
    }
}
