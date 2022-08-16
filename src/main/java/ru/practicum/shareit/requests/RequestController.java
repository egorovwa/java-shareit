package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestDtoMaper;

import javax.validation.Valid;

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
}
