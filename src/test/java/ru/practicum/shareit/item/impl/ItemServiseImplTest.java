package ru.practicum.shareit.item.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoMaper;
import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.NotUsedCommentException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDtoMaper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.item.dto.ItemDtoWithBoking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.RequestService;
import ru.practicum.shareit.user.UserServise;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static ru.practicum.shareit.Entitys.*;

@ExtendWith(MockitoExtension.class)
class ItemServiseImplTest {
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
    UserServise userServise = Mockito.mock(UserServise.class);
    BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    RequestService requestService = Mockito.mock(RequestService.class);
    CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    CommentDtoMaper commentDtoMaper = new CommentDtoMaper();
    BookingDtoMaper bookingDtoMaper = new BookingDtoMaper();
    ItemDtoMaper itemDtoMaper = new ItemDtoMaper(bookingDtoMaper);
    ItemServiseImpl itemServise = new ItemServiseImpl(userServise, itemRepository, bookingRepository,
            commentRepository, itemDtoMaper, commentDtoMaper, requestService);

    private static ItemDto testItemDtoWithOutRequest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        return itemDto;
    }

    private static Item testItemId1User1() {
        return new Item(1L, "name", "d", true, USER_ID1);
    }

    @Test
    void test1_createItem_withOutRequest() throws ModelNotExitsException, IncorrectUserIdException { // TODO: 18.08.2022 безсмыссленно в интегр
        Item item = new Item(1L, "item", "Description", true, USER_ID1);
        Mockito
                .when(itemRepository.save(Mockito.any()))
                .thenReturn(item);

        ItemDto itemDto = testItemDtoWithOutRequest();


        Item savedItem = itemServise.createItem(1L, itemDto);

        assertThat(savedItem.getName(), is(itemDto.getName()));
        assertThat(savedItem.getDescription(), is(itemDto.getDescription()));
        assertThat(savedItem.getOwner(), is(USER_ID1));
        assertThat(savedItem.getAvailable(), is(true));
        assertNull(savedItem.getRequest());
    }

    @Test
    void test1_2_createItem_userNotFound() throws ModelNotExitsException, IncorrectUserIdException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        ItemDto itemDto = testItemDtoWithOutRequest();
        assertThrows(IncorrectUserIdException.class, () -> itemServise.createItem(1L, itemDto));
    }

    @Test
    void test1_3_createItem_withRequest() throws ModelNotExitsException, IncorrectUserIdException {
        ItemDto itemDto = testItemDtoWithOutRequest();
        itemDto.setRequestId(1L);
        Item itemMustBeSaved = new Item();
        itemMustBeSaved.setName(itemDto.getName());
        itemMustBeSaved.setOwner(USER_ID2);
        itemMustBeSaved.setDescription(itemDto.getDescription());
        itemMustBeSaved.setRequest(ITEMREQUEST_ID1_USER1);
        itemMustBeSaved.setAvailable(true);

        Mockito
                .when(userServise.findById(anyLong()))
                .thenReturn(USER_ID2);
        Mockito.when(requestService.findById(1L))
                .thenReturn(ITEMREQUEST_ID1_USER1);
        itemServise.createItem(2, itemDto);
        Mockito.verify(itemRepository, Mockito.times(1)).save(itemMustBeSaved);
    }

    @Test
    void test2_1_patchItem_incorrectUserId() {
        Item item = testItemId1User1();
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        assertThrows(IncorectUserOrItemIdException.class, () -> itemServise.patchItem(2, 1,
                Mockito.any()));
    }

    @Test
    void test2_2_pathItem_incorrectIemId() {
        Mockito.when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotExitsException.class, () -> itemServise.patchItem(1, 1, testItemId1User1()));
    }

    @Test
    void test2_3_pathItem() throws ModelNotExitsException, IncorectUserOrItemIdException {
        Item toUpdateItem = new Item();
        toUpdateItem.setAvailable(false);
        toUpdateItem.setDescription("udated");
        toUpdateItem.setName("updatedName");
        Mockito.when(userServise.findById(1))
                .thenReturn(USER_ID1);
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(testItemId1User1()));
        itemServise.patchItem(1, 1, toUpdateItem);
        toUpdateItem.setId(1L);
        toUpdateItem.setOwner(USER_ID1);

        Mockito.verify(itemRepository, Mockito.times(1)).save(toUpdateItem);
    }

    @Test
    void test3_1_findById_withItemNotFound() {
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotExitsException.class, () -> itemServise.findById(1, 1));
    }

    @Test
    void test3_2_findById_forOwner() throws ModelNotExitsException {
        Item item = testItemId1User1();
        ItemDtoWithBoking withBoking = new ItemDtoWithBoking(1L, item.getName(), item.getDescription(), true,
                USER_ID1, bookingDtoMaper.toItemDto(Optional.of(BOOKING_FIST)), bookingDtoMaper
                .toItemDto(Optional.of(BOOKING_NEXT)), List.of(commentDtoMaper.toDto(COMMENTID1_USER2)));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .when(bookingRepository.findLastBookingToItem(1, TEST_TIME_LONG))
                .thenReturn(List.of(BOOKING_FIST));
        Mockito
                .when(bookingRepository.findNextBookingToItem(1, TEST_TIME_LONG))
                .thenReturn(List.of(BOOKING_NEXT));
        Mockito
                .when(commentRepository.findByItem_IdOrderByCreatedDesc(1L))
                .thenReturn(List.of(COMMENTID1_USER2));
        ItemDtoWithBoking result = itemServise.findById(1L, 1L);

        assertThat(result, is(withBoking));
    }

    @Test
    void test3_3_findById_forOther() throws ModelNotExitsException {
        Item item = testItemId1User1();
        ItemDtoWithBoking withBoking = new ItemDtoWithBoking(1L, item.getName(), item.getDescription(), true,
                USER_ID1, null, null, List.of(commentDtoMaper.toDto(COMMENTID1_USER2)));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(item));
        Mockito
                .verify(bookingRepository, Mockito.times(0))
                .findLastBookingToItem(Mockito.anyLong(), Mockito.anyLong());
        Mockito
                .verify(bookingRepository, Mockito.times(0))
                .findNextBookingToItem(Mockito.anyLong(), Mockito.anyLong());
        Mockito
                .when(commentRepository.findByItem_IdOrderByCreatedDesc(1L))
                .thenReturn(List.of(COMMENTID1_USER2));
        ItemDtoWithBoking result = itemServise.findById(1, 2);
        assertThat(result, is(withBoking));
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());

    }

    @Test
    void test4_FindById_byItemId_ItemNotFound() {
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotExitsException.class, () -> itemServise.findById(1L));
    }

    @Test
    void test5_findAllByOwnerId() {
        Item item = testItemId1User1();
        ItemDtoWithBoking withBoking = new ItemDtoWithBoking(1L, item.getName(), item.getDescription(), true,
                USER_ID1, bookingDtoMaper.toItemDto(Optional.of(BOOKING_FIST)), bookingDtoMaper
                .toItemDto(Optional.of(BOOKING_NEXT)), List.of(commentDtoMaper.toDto(COMMENTID1_USER2)));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(Pageable.ofSize(5), 1L))
                .thenReturn(new PageImpl<>(List.of(item)));
        Mockito
                .when(bookingRepository.findLastBookingToItem(1, TEST_TIME_LONG))
                .thenReturn(List.of(BOOKING_FIST));
        Mockito
                .when(bookingRepository.findNextBookingToItem(1, TEST_TIME_LONG))
                .thenReturn(List.of(BOOKING_NEXT));
        Mockito
                .when(commentRepository.findByItem_IdOrderByCreatedDesc(1L))
                .thenReturn(List.of(COMMENTID1_USER2));
        assertThat(itemServise.findAllByOwnerId(1L, 0, 5).stream().findFirst().get(), is(withBoking));
        Mockito.verify(itemRepository, Mockito.times(1))
                .findByOwnerIdOrderByIdAsc(Pageable.ofSize(5), 1L);

    }

    @Test
    void test5_1findAllByOwnerId_withOutPage() {
        Item item = testItemId1User1();
        ItemDtoWithBoking withBoking = new ItemDtoWithBoking(1L, item.getName(), item.getDescription(), true,
                USER_ID1, bookingDtoMaper.toItemDto(Optional.of(BOOKING_FIST)), bookingDtoMaper
                .toItemDto(Optional.of(BOOKING_NEXT)), List.of(commentDtoMaper.toDto(COMMENTID1_USER2)));
        Mockito
                .when(itemRepository.findByOwnerIdOrderByIdAsc(1L))
                .thenReturn(List.of(item));
        Mockito
                .when(bookingRepository.findLastBookingToItem(1, TEST_TIME_LONG))
                .thenReturn(List.of(BOOKING_FIST));
        Mockito
                .when(bookingRepository.findNextBookingToItem(1, TEST_TIME_LONG))
                .thenReturn(List.of(BOOKING_NEXT));
        Mockito
                .when(commentRepository.findByItem_IdOrderByCreatedDesc(1L))
                .thenReturn(List.of(COMMENTID1_USER2));
        assertThat(itemServise.findAllByOwnerId(1L, null, null).stream().findFirst().get(), is(withBoking));
        Mockito.verify(itemRepository, Mockito.times(1))
                .findByOwnerIdOrderByIdAsc(1L);

    }

    @Test
    void test6_1findByText_notBlankText() {

        Mockito
                .when(itemRepository.findByText(Pageable.ofSize(5), "text"))
                .thenReturn(new PageImpl(List.of(testItemId1User1())));
        assertEquals(1, itemServise.findByText("text", 0, 5).size());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findByText(Pageable.ofSize(5), "text");
    }

    @Test
    void test6_2findByText_notBlankText_withOutFromSize() {

        Mockito
                .when(itemRepository.findByText("text"))
                .thenReturn(List.of(testItemId1User1()));
        assertEquals(1, itemServise.findByText("text", null, null).size());
        Mockito.verify(itemRepository, Mockito.times(1))
                .findByText("text");
    }

    @Test
    void test6_3findByText_WithOutText_withOutPage() {
        assertEquals(0, itemServise.findByText("", null, null).size());
    }

    @Test
    void test6_4findByText_WithOutText() {
        assertEquals(0, itemServise.findByText(null, 0, 5).size());
    }

    @Test
    void test7_1_addComment_userNotFound() throws ModelNotExitsException {
        Mockito
                .when(userServise.findById(1L))
                .thenThrow(ModelNotExitsException.class);
        assertThrows(ModelNotExitsException.class, () -> itemServise.addComment(1L, 1L, "text"));
    }

    @Test
    void test7_2_addComment_itemNotFound() {
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotExitsException.class, () -> itemServise.addComment(1L, 1L, "text"));
    }

    @Test
    void test7_3_addComment_NotUsed() throws ModelNotExitsException, NotUsedCommentException {
        Mockito
                .when(bookingRepository.usedCount(1L, 1L, TEST_TIME_LONG))
                .thenReturn(0);
        Mockito
                .when(userServise.findById(1L))
                .thenReturn(USER_ID1);
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(testItemId1User1()));
        assertThrows(NotUsedCommentException.class, () -> itemServise.addComment(1L, 1, "text"));
    }

    @Test
    void test7_4_addComment_dateTimeNow() throws ModelNotExitsException, NotUsedCommentException {  // TODO: 25.08.2022 убрать срабатывает через раз
        Mockito
                .when(bookingRepository.usedCount(2L, 1L, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)))
                .thenReturn(1);
        Mockito
                .when(userServise.findById(2L))
                .thenReturn(USER_ID2);
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.of(testItemId1User1()));
        Comment comment = new Comment(null, "text", testItemId1User1(),
                USER_ID2, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        comment.setAuthor(USER_ID2);
        itemServise.addComment(1L, 2L, "text");
        Mockito
                .verify(commentRepository, Mockito.times(1)).save(comment);

    }
}