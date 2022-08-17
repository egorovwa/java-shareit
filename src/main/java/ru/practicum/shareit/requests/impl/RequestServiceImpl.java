package ru.practicum.shareit.requests.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.requests.RequestService;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserServise userServise;
    private final RequestRepository requestRepository;

    @Override
    public ItemRequest createRequest(Long userId, ItemRequestDto itemRequestDto) throws ModelNotExitsException {   // TODO: 16.08.2022 пока без мапера
        User user = userServise.findById(userId);
        ItemRequest itemRequest = new ItemRequest(itemRequestDto.getDescription(), user);
        return requestRepository.save(itemRequest);
    }

    @Override
    public Collection<ItemRequest> findAllWithPage(Integer from, Integer size) { // TODO: 17.08.2022 проверить как отработает from c 0
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        return requestRepository.findAll(pageable).toList();

    }
}
