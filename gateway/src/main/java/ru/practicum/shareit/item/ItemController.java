package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * // TODO .
 */
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        log.info("Creating item {} userId = {}", itemDto, userId);
        return itemClient.postItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated({Marker.OnPatch.class})
    public ResponseEntity<Object> patchItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                                            @Valid @RequestBody ItemDto itemDto) {
        log.info("Patch itemid = {}, userId = {}, inData {}", itemId, userId, itemDto);
        return itemClient.pacthItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Get itemId = {}, userId = {}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all item by ownerId = {}, from = {}, size = {}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByText(@RequestParam(name = "text") String text, // TODO: 08.09.2022 или NotBlank ???
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get all item by text = {}, userId= {} from = {}, size = {}", text, userId, from, size);
        return itemClient.getItems("/search", text, size, from, userId);
    }


    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid CommentDto text) {
        String path = "/" + itemId + "/comment";
        log.info("Add comment {}, userId = {}, itemId = {}", text, userId, itemId);
        return itemClient.postComment(path, userId, text);
    }
}