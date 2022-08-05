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

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemDto itemDto)
            throws ModelNotExitsException, IncorrectUserIdException {
        return ItemDtoMaper.toDto(itemServise.createItem(userId, ItemDtoMaper.fromDto(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                             @RequestBody ItemDto itemDto)
            throws ModelNotExitsException, IncorectUserOrItemIdException {
        return ItemDtoMaper.toDto(itemServise.patchItem(userId, itemId, ItemDtoMaper.fromDto(itemDto)));
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBoking findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId)
            throws ModelNotExitsException {
        return itemServise.findById(itemId,userId);
    }

    @GetMapping
    public Collection<ItemDtoWithBoking> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemServise.findAllByOwnerId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> findByText(@RequestParam String text) {
        return itemServise.findByText(text).stream()
                .map(ItemDtoMaper::toDto)
                .collect(Collectors.toList());
    }
    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Valid CommentDto text) throws ModelNotExitsException, NotUsedCommentException {
        Comment comment = itemServise.addComment(itemId, userId, text.getText());
        return CommentDtoMaper.toDto(comment) ;
    }
}
