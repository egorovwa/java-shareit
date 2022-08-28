package ru.practicum.shareit.requests.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.Entitys.*;

class ItemRequestDtoMaperTest {
    final ItemRequestDtoMaper itemRequestDtoMaper = new ItemRequestDtoMaper(new ItemDtoMaper(new BookingDtoMaper()));

    @Test
    void toCreatedDto() {
        ItemRequest itemRequest = new ItemRequest(1L, "sss", USER_ID2,
                TEST_TIME_LONG, List.of(ITEM_ID1_OWNER1_AVALIBLE_TRUE));
        ItemRequestDto itemRequestDto = new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                USER_ID2, TEST_TIME_DATE_TIME);
        assertThat(itemRequestDtoMaper.toCreatedDto(itemRequest), is(itemRequestDto));
    }

    @Test
    void fromDto() {
        ItemRequest itemRequestCreated = new ItemRequest();
        itemRequestCreated.setRequestor(USER_ID2);
        itemRequestCreated.setDescription("sss");
        itemRequestCreated.setCreated(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("sss");
        itemRequestDto.setRequestor(USER_ID2);
        assertThat(itemRequestDtoMaper.fromDto(itemRequestDto), is(itemRequestCreated));
    }

    @Test
    void toDtoForRequestor() {
        ItemRequest itemRequest = new ItemRequest(1L, "sss", USER_ID2, TEST_TIME_LONG,
                List.of(ITEM_ID1_OWNER1_AVALIBLE_TRUE));
        ItemDtoMaper itemDtoMaper = new ItemDtoMaper(new BookingDtoMaper());

        ItemRequestDtoForRequestor dto = new ItemRequestDtoForRequestor(1L, "sss",
                USER_ID2, TEST_TIME_DATE_TIME, List.of(itemDtoMaper.toDto(ITEM_ID1_OWNER1_AVALIBLE_TRUE)));
        assertThat(itemRequestDtoMaper.toDtoForRequestor(itemRequest), is(dto));
    }
}