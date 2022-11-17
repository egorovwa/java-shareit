package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.request.dto.ItemRequestDtoToCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequesstController {
    private final ItemRequestsClient requestsClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDtoToCreate itemRequestDtoToCreate,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create request {}, userId = {}", itemRequestDtoToCreate, userId);
        return requestsClient.postRequest(userId, itemRequestDtoToCreate);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllItemRequest(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all requests, userId = {}, from = {}, size = {}", userId, from, size);
        return requestsClient.getRequests("/all", userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> findAllForRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all request for requestorId = {}", userId);
        return requestsClient.getRequests(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemRequest(@PathVariable("itemId") Long itemId,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get itemId = {}, userId = {}", itemId, userId);
        return requestsClient.getRequests(userId, itemId);
    }


}
