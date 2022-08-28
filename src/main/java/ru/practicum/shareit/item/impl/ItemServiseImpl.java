package ru.practicum.shareit.item.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.NotUsedCommentException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServise;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.RequestService;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;
import ru.practicum.shareit.util.PageParam;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiseImpl implements ItemServise {
    private final UserServise userServise;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemDtoMaper itemDtoMaper;
    private final CommentDtoMaper commentDtoMaper;
    private final RequestService requestService;

    @Override
    public Item createItem(long userId, ItemDto itemDto) throws IncorrectUserIdException {
        Item item = itemDtoMaper.fromDto(itemDto);
        try {
            User user = userServise.findById(userId);
            item.setOwner(user);
            log.info("Добавленна item ({}), пользователем id {}", item.getName(), userId);
            if (itemDto.getRequestId() != null) {
                ItemRequest itemRequest = requestService.findById(itemDto.getRequestId());
                item.setRequest(itemRequest);
                itemRequest.getItems().add(item);
            }
            return itemRepository.save(item);
        } catch (ModelNotExitsException e) {
            log.warn("Попытка добавление item ({}), несуществующим пользователем id {}", item.getName(), userId);
            throw new IncorrectUserIdException("Пользователь не найден", String.valueOf(userId));
        }
    }

    @Override
    public Item patchItem(long userId, long itemId, Item item) throws ModelNotExitsException,
            IncorectUserOrItemIdException {
        Item updatedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("Вещь не найденна", "id", String.valueOf(itemId)));
        if (updatedItem.getOwner().equals(userServise.findById(userId))) {
            Optional.ofNullable(item.getName()).ifPresent((name) -> {
                log.info("Обновление имени вещи id {} newName = {}", updatedItem.getId(), item.getName());
                updatedItem.setName(item.getName());
            });
            Optional.ofNullable(item.getDescription()).ifPresent((description) -> {
                log.info("Обновление описания вещи id {} newDescription = {}", updatedItem.getId(), item.getDescription());
                updatedItem.setDescription(item.getDescription());
            });
            Optional.ofNullable(item.getAvailable()).ifPresent((available) -> {
                log.info("Обновление Available вещи id {} newAvailable = {}", updatedItem.getId(), item.getAvailable());
                updatedItem.setAvailable(item.getAvailable());
            });
            return itemRepository.save(updatedItem);
        } else {
            log.warn("Пользователь id {} пытается изменить вещь id {}", userId, itemId);
            throw new IncorectUserOrItemIdException("Вещь не принадлежит пользователю", userId, itemId);
        }

    }

    @Override
    public ItemDtoWithBoking findById(long itemId, long userId) throws ModelNotExitsException {
        log.info("поиск вещи id ={}", itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("Вещь не найденна", "id", String.valueOf(itemId)));
        if (item.getOwner().getId() == userId) {
            Optional<Booking> lastBooking = getLastBooking(itemId);
            Optional<Booking> nextBooking = getNextBooking(itemId);
            Collection<CommentDto> comments = getItemComments(itemId);
            return itemDtoMaper.toDtoWithBooking(item, lastBooking, nextBooking, comments);
        } else {
            return itemDtoMaper.toDtoWithBooking(item, getItemComments(itemId));
        }
    }

    private Collection<CommentDto> getItemComments(long itemId) {
        Collection<Comment> comments = new ArrayList<>(commentRepository.findByItem_IdOrderByCreatedDesc(itemId));
        return comments.stream().map(commentDtoMaper::toDto).collect(Collectors.toList());
    }

    private Optional<Booking> getNextBooking(long itemId) {
        return bookingRepository.findNextBookingToItem(itemId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .stream().min(Comparator.comparing(Booking::getStart));
    }

    private Optional<Booking> getLastBooking(long itemId) {
        return bookingRepository.findLastBookingToItem(itemId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                .stream().max(Comparator.comparing(Booking::getEnd));
    }

    @Override
    public Item findById(long itemId) throws ModelNotExitsException {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("Вещь не найденна", "id", String.valueOf(itemId)));
    }

    @Override
    public Collection<ItemDtoWithBoking> findAllByOwnerId(long userId, PageParam pageParam) {
        if (pageParam != null) {
            Pageable pageable = PageRequest.of(pageParam.getPage(), pageParam.getSize());
            log.info("поиск вещей пользователя id ={}", userId);
            return itemRepository.findByOwnerIdOrderByIdAsc(pageable, userId).stream().map(i -> itemDtoMaper
                            .toDtoWithBooking(i, getLastBooking(i.getId()), getNextBooking(i.getId()),
                                    getItemComments(i.getId())))
                    .collect(Collectors.toUnmodifiableList());
        } else {
            return itemRepository.findByOwnerIdOrderByIdAsc(userId).stream().map(i -> itemDtoMaper
                            .toDtoWithBooking(i, getLastBooking(i.getId()), getNextBooking(i.getId()),
                                    getItemComments(i.getId())))
                    .collect(Collectors.toUnmodifiableList());
        }
    }

    @Override
    public Collection<Item> findByText(String text, PageParam pageParam) {
        if (pageParam != null) {
            Pageable pageable = PageRequest.of(pageParam.getPage(), pageParam.getSize());
            if (Strings.isNotBlank(text)) {
                log.info("поиск вещей по тексту ({})", text);
                return Collections.unmodifiableCollection(itemRepository.findByText(pageable, text).toList());
            } else return new ArrayList<>();
        } else {
            if (Strings.isNotBlank(text)) {
                log.info("поиск вещей по тексту ({})", text);
                return Collections.unmodifiableCollection(itemRepository.findByText(text));
            } else return new ArrayList<>();
        }
    }

    @Override
    public Comment addComment(Long itemId, long userId, String text) throws ModelNotExitsException, NotUsedCommentException {
        Item item = findById(itemId);
        User user = userServise.findById(userId);
        int i = bookingRepository.usedCount(userId, itemId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        if (bookingRepository.usedCount(userId, itemId, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) >= 1) {
            return commentRepository.save(new Comment(null, text, item, user,
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));
        } else {
            throw new NotUsedCommentException("пользователь не пользовался вещью", userId, itemId);
        }
    }
}
