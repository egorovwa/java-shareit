package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoMaper;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;
    private final ItemRequestDtoMaper maper;

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) throws ModelNotExitsException {
        return maper.toCreatedDto(requestService.createRequest(userId, itemRequestDto));
    }

    @GetMapping("/all")
    private Collection<ItemRequestDto> findAllItemRequest(@RequestParam("from") Integer from,
                                                          @RequestParam("size") Integer size) {
        return requestService.findAllWithPage(from, size).stream()
                .map(maper::toCreatedDto).collect(Collectors.toList());
    }
}
