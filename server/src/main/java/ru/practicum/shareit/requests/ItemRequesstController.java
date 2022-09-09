package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.IncorrectPageValueException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoForRequestor;
import ru.practicum.shareit.requests.dto.ItemRequestDtoMaper;
import ru.practicum.shareit.util.PageParam;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequesstController {
    private final RequestService requestService;
    private final ItemRequestDtoMaper maper;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) throws ModelNotExitsException {
        return maper.toCreatedDto(requestService.createRequest(userId, itemRequestDto));
    }

    @GetMapping("/all")
    public Collection<ItemRequestDtoForRequestor> findAllItemRequest(
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) throws ModelNotExitsException, IncorrectPageValueException {
        return requestService.findAllWithPage(PageParam.createPageable(from, size, "created"), userId).stream()
                .map(maper::toDtoForRequestor).collect(Collectors.toList());
    }

    @GetMapping
    public Collection<ItemRequestDtoForRequestor> findAllForRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) throws ModelNotExitsException {
        return requestService.findAllForRequestor(userId).stream()
                .map(maper::toDtoForRequestor).collect(Collectors.toList());
    }

    @GetMapping("/{itemId}")
    public ItemRequestDtoForRequestor findItemRequest(@PathVariable("itemId") Long itemId,
                                                      @RequestHeader("X-Sharer-User-Id") Long userId) throws ModelNotExitsException {
        return maper.toDtoForRequestor(requestService.findItemRequest(itemId, userId));
    }


}
