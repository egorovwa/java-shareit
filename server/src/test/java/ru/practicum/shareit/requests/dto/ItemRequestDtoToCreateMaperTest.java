package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.contract.request.dto.ItemRequestDto;
import ru.practicum.contract.request.dto.ItemRequestDtoForRequestor;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoMaper;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.TestConstants.*;

class ItemRequestDtoToCreateMaperTest {
    private final UserDtoMaper userDtoMaper = new UserDtoMaper();
    private final BookingDtoMaper bookingDtoMaper = new BookingDtoMaper();
    private final ItemDtoMaper itemDtoMaper = new ItemDtoMaper(bookingDtoMaper, userDtoMaper);
    final ItemRequestDtoMaper itemRequestDtoMaper = new ItemRequestDtoMaper(itemDtoMaper, userDtoMaper);

    @Test
    void toCreatedDto() {
        ItemRequest itemRequest = new ItemRequest(1L, "sss", USER_ID2,
                TEST_TIME_LONG, List.of(ITEM_ID1_OWNER1_AVALIBLE_TRUE));
        ItemRequestDto itemRequestDto = new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                userDtoMaper.toDto(USER_ID2), TEST_TIME_DATE_TIME);
        assertThat(itemRequestDtoMaper.toCreatedDto(itemRequest), is(itemRequestDto));
    }


    @Test
    void toDtoForRequestor() {
        ItemRequest itemRequest = new ItemRequest(1L, "sss", USER_ID2, TEST_TIME_LONG,
                List.of(ITEM_ID1_OWNER1_AVALIBLE_TRUE));

        ItemRequestDtoForRequestor dto = new ItemRequestDtoForRequestor(1L, "sss",
                userDtoMaper.toDto(USER_ID2), TEST_TIME_DATE_TIME, List.of(itemDtoMaper.toDtoCreated(ITEM_ID1_OWNER1_AVALIBLE_TRUE)));
        assertThat(itemRequestDtoMaper.toDtoForRequestor(itemRequest), is(dto));
    }
}