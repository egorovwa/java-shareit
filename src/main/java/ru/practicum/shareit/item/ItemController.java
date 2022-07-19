package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.Collection;

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
return itemServise.createItem(userId, itemDto);
    }
    @PatchMapping("/{itemId}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,@PathVariable long itemId,
                             @RequestBody ItemDto itemDto)
            throws ModelNotExitsException, IncorectUserOrItemIdException {
       return itemServise.patchItem(userId,itemId,itemDto);
    }
    @GetMapping("/{itemId}")
    public ItemDto findById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId)
            throws ModelNotExitsException {
       return itemServise.findByIdDto(itemId);
    }
    @GetMapping
    public Collection<ItemDto> findAllByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId){
        return itemServise.findAllByOwnerId(userId);
    }
    @GetMapping("/search")
    public Collection<ItemDto> findByText(@RequestParam String text){
        return itemServise.findByText(text);
    }
}
