package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.util.PageParam;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * // TODO .
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServise itemServise;
    private final ItemDtoMaper itemDtoMaper;
    private final CommentDtoMaper commentDtoMaper;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto)
            throws IncorrectUserIdException, RequestNotExistException {
        return itemDtoMaper.toDto(itemServise.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                             @RequestBody ItemDto itemDto)
            throws ModelNotExitsException, IncorectUserOrItemIdException {
        return itemDtoMaper.toDto(itemServise.patchItem(userId, itemId, itemDtoMaper.fromDto(itemDto)));
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBoking findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId)
            throws ModelNotExitsException {
        return itemServise.findById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDtoWithBoking> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @PathParam("from") Integer from,
                                                          @PathParam("size") Integer size) throws IncorrectPageValueException {
        PageParam pageParam = PageParam.create(from, size);
        return itemServise.findAllByOwnerId(userId, pageParam);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findByText(@RequestParam String text, @PathParam("from") Integer from,
                                          @PathParam("size") Integer size) throws IncorrectPageValueException {
        PageParam pageParam = PageParam.create(from, size);
        return itemServise.findByText(text, pageParam).stream()
                .map(itemDtoMaper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Valid CommentDto text) throws ModelNotExitsException, NotUsedCommentException {
        Comment comment = itemServise.addComment(itemId, userId, text.getText());
        return commentDtoMaper.toDto(comment);
    }
}
