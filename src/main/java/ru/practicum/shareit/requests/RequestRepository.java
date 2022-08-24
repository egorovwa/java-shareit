package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.Collection;
import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findAllByRequestorIdIsNot(Pageable pageable, Long userId);
    List<ItemRequest> findItemRequestsByRequestorIdOrderByCreatedDesc(long userId);
   List<ItemRequest> findAllByRequestorIdIsNotOrderByCreatedDesc(Long userId);
}
