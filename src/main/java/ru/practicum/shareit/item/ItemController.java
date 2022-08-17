package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.NotUsedCommentException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.Valid;
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
            throws IncorrectUserIdException {
        return itemDtoMaper.toDto(itemServise.createItem(userId, itemDtoMaper.fromDto(itemDto)));
    }
    @PostMapping


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
    public Collection<ItemDtoWithBoking> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemServise.findAllByOwnerId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findByText(@RequestParam String text) {
        return itemServise.findByText(text).stream()
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
