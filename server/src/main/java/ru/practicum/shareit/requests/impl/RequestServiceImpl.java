package ru.practicum.shareit.requests.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.contract.request.dto.ItemRequestDtoToCreate;
import ru.practicum.shareit.booking.exceptions.UserNotFoundExteption;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.RequestNotExistException;
import ru.practicum.shareit.requests.RequestRepository;
import ru.practicum.shareit.requests.RequestService;
import ru.practicum.contract.request.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final UserServise userServise;
    private final RequestRepository requestRepository;

    @Override
    public ItemRequest createRequest(Long userId, ItemRequestDtoToCreate itemRequestDtoToCreate) throws ModelNotExitsException {
        User user = userServise.findById(userId);
        ItemRequest itemRequest = new ItemRequest(itemRequestDtoToCreate.getDescription(), user);
        log.info(" Ползователь Создал запрос  userid= {}", userId);
        return requestRepository.save(itemRequest);
    }

    @Override
    public Collection<ItemRequest> findAllWithPage(Pageable pageable, Long userId) throws ModelNotExitsException {
        try {

            User user = userServise.findById(userId);
        } catch (ModelNotExitsException e) {
            log.warn(" Поытка получить запросы от несуществующего пользователя id {}", userId);
            throw new UserNotFoundExteption("пользователь не чсуществует", "id",
                    userId.toString());
        }
        if (pageable != null) {
            log.info("Получение запросов полбзователем id {}", userId);
            return requestRepository.findAllByRequestorIdIsNot(pageable, userId).toList();
        } else {
            log.info("Получение запросов полбзователем id {}", userId);
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
        log.info("Запрос сохранен  {}", itemRequest);
        return requestRepository.save(itemRequest);

    }

    @Override
    public Collection<ItemRequest> findAllForRequestor(Long userId) throws ModelNotExitsException {
        User user = userServise.findById(userId);
        log.info("Получение пользователем {} его запросов", userId);
        return requestRepository.findItemRequestsByRequestorIdOrderByCreatedDesc(userId);
    }

    @Override
    public ItemRequest findItemRequest(Long itemId, Long userId) throws ModelNotExitsException {
        User user = userServise.findById(userId);
        log.info("Получение пользователем {} всех запросов", userId);
        return requestRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotExitsException("запрос не существует", "id", itemId.toString()));
    }
}
