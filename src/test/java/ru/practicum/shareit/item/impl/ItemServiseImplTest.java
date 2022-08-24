package ru.practicum.shareit.item.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServise;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDtoMaper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.RequestService;
import ru.practicum.shareit.user.UserServise;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static ru.practicum.shareit.Entitys.*;
@ExtendWith(MockitoExtension.class)
class ItemServiseImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserServise userServise;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    RequestService requestService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    ItemDtoMaper dtoMaper;
    @Mock
    CommentDtoMaper commentDtoMaper;
    ItemServiseImpl itemServise = new ItemServiseImpl(userServise,itemRepository,bookingRepository,
            commentRepository,dtoMaper,commentDtoMaper,requestService);

    @Test
    void test1_createItem_withOutRequest() throws ModelNotExitsException, IncorrectUserIdException { // TODO: 18.08.2022 безсмыссленно в интегр
        Mockito.when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        
        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);

        
        Item savedItem = itemServise.createItem(1L, itemDto);

        assertThat(savedItem.getName(),is(itemDto.getName()));
        assertThat(savedItem.getDescription(),is(itemDto.getDescription()));
        
    }

    @Test
    void patchItem() {
    }

    @Test
    void findById() {
    }

    @Test
    void testFindById() {
    }

    @Test
    void findAllByOwnerId() {
    }

    @Test
    void findByText() {
    }

    @Test
    void addComment() {
    }
}