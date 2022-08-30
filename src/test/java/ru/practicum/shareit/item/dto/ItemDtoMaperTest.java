package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.TestConstants.*;

class ItemDtoMaperTest {
    final ItemDtoMaper itemDtoMaper = new ItemDtoMaper(new BookingDtoMaper());

    @Test
    void fromDto() {
        ItemDto itemDto = new ItemDto(1L, "email@mail.com", "sss", true, USER_ID1, 1L);
        Item item = new Item(1L, "email@mail.com", "sss", true, USER_ID1);
        assertThat(itemDtoMaper.fromDto(itemDto), is(item));
    }

    @Test
    void toDto_withRequest() {
        Item item = new Item(1L, "email@mail.com", "sss", true, USER_ID1, ITEMREQUEST_ID1_USER1);
        ItemDto itemDto = new ItemDto(1L, "email@mail.com", "sss", true, USER_ID1, 1L);
        assertThat(itemDtoMaper.toDto(item), is(itemDto));
    }

    @Test
    void toDto_withOutRequest() {
        Item item = new Item(1L, "email@mail.com", "sss", true, USER_ID1);
        ItemDto itemDto = new ItemDto(1L, "email@mail.com", "sss", true, USER_ID1);
        assertThat(itemDtoMaper.toDto(item), is(itemDto));
    }

    @Test
    void toDtoWithBooking() {
        BookingDtoMaper bookingDtoMaper = new BookingDtoMaper();
        CommentDtoMaper commentDtoMaper = new CommentDtoMaper();
        Item item = new Item(1L, "email@mail.com", "sss", true, USER_ID1, ITEMREQUEST_ID1_USER1);

        ItemDtoWithBoking dto = new ItemDtoWithBoking(1L, "email@mail.com", "sss", true,
                USER_ID1, bookingDtoMaper.toItemDto(Optional.of(BOOKING_FIST)),
                bookingDtoMaper.toItemDto(Optional.of(BOOKING_NEXT)),
                List.of(commentDtoMaper.toDto(COMMENTID1_USER2)));
        assertThat(itemDtoMaper.toDtoWithBooking(item, Optional.of(BOOKING_FIST), Optional.of(BOOKING_NEXT),
                List.of(commentDtoMaper.toDto(COMMENTID1_USER2))), is(dto));

    }

    @Test
    void testToDtoWithBooking() {
        BookingDtoMaper bookingDtoMaper = new BookingDtoMaper();
        CommentDtoMaper commentDtoMaper = new CommentDtoMaper();
        Item item = new Item(1L, "email@mail.com", "sss", true, USER_ID1, ITEMREQUEST_ID1_USER1);

        ItemDtoWithBoking dto = new ItemDtoWithBoking(1L, "email@mail.com", "sss", true,
                USER_ID1, List.of(commentDtoMaper.toDto(COMMENTID1_USER2)));
        assertThat(itemDtoMaper.toDtoWithBooking(item, List.of(commentDtoMaper.toDto(COMMENTID1_USER2))), is(dto));

    }
}
