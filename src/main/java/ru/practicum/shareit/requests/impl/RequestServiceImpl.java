package ru.practicum.shareit.requests.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.exceptions.UserNotFoundExteption;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.RequestNotExistException;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.requests.RequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;
import ru.practicum.shareit.util.PageParam;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserServise userServise;
    private final RequestRepository requestRepository;

    @Override
    public ItemRequest createRequest(Long userId, ItemRequestDto itemRequestDto) throws ModelNotExitsException {
        User user = userServise.findById(userId);
        ItemRequest itemRequest = new ItemRequest(itemRequestDto.getDescription(), user);
        return requestRepository.save(itemRequest);
    }

    @Override
    public Collection<ItemRequest> findAllWithPage(PageParam pageParam, Long userId) throws ModelNotExitsException {
        try {

            User user = userServise.findById(userId);
        } catch (ModelNotExitsException e) {
            throw new UserNotFoundExteption("пользователь не чсуществует", "id",
                    userId.toString());
        }
        if (pageParam != null) {
            Pageable pageable = PageRequest.of(pageParam.getPage(), pageParam.getSize(),
                    Sort.by("created").descending());
            return requestRepository.findAllByRequestorIdIsNot(pageable, userId).toList();
        } else {
            return requestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(userId);
        }

    }

    @Override
    public ItemRequest findById(Long requestId) throws RequestNotExistException {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotExistException("Запрос не найден", "id", requestId.toString()));
    }

    @Override
    public ItemRequest save(ItemRequest itemRequest) {
        return requestRepository.save(itemRequest);

    }

    @Override
    public Collection<ItemRequest> findAllForRequestor(Long userId) throws ModelNotExitsException {
        User user = userServise.findById(userId);
        return requestRepository.findItemRequestsByRequestorIdOrderByCreatedDesc(userId);
    }

    @Override
    public ItemRequest findItemRequest(Long itemId, Long userId) throws ModelNotExitsException {
        User user = userServise.findById(userId);

        return requestRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("запрос не существует", "id", itemId.toString()));
    }
}
