package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.item.dto.CommentDto;
import ru.practicum.contract.item.dto.ItemDto;
import ru.practicum.contract.item.dto.ItemDtoCreated;
import ru.practicum.contract.request.dto.ItemRequestDtoToCreate;
import ru.practicum.shareit.item.dto.CommentDtoMaper;
import ru.practicum.contract.item.dto.ItemDtoWithBoking;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.dto.ItemDtoMaper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.util.PageParam;

import javax.websocket.server.PathParam;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServise itemServise;
    private final ItemDtoMaper itemDtoMaper;
    private final CommentDtoMaper commentDtoMaper;

    @PostMapping
    public ItemDtoCreated createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto)
            throws IncorrectUserIdException {
        return itemDtoMaper.toDtoCreated(itemServise.createItem(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDtoCreated patchItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId,
                             @RequestBody ItemDto itemDto)
            throws ModelNotExitsException, IncorectUserOrItemIdException {
        return itemDtoMaper.toDtoCreated(itemServise.patchItem(userId, itemId, itemDtoMaper.fromDto(itemDto)));
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
        return itemServise.findAllByOwnerId(userId, PageParam.createPageable(from, size));
    }

    @GetMapping("/search")
    public Collection<ItemDtoCreated> findByText(@RequestParam String text, @PathParam("from") Integer from,
                                          @PathParam("size") Integer size) throws IncorrectPageValueException {
        return itemServise.findByText(text, PageParam.createPageable(from, size)).stream()
                .map(itemDtoMaper::toDtoCreated)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") Long itemId,
                                 @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody CommentDto text) throws ModelNotExitsException, NotUsedCommentException {
        Comment comment = itemServise.addComment(itemId, userId, text.getText());
        return commentDtoMaper.toDto(comment);
    }
}
