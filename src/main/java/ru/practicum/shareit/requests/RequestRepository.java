package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findAll(Pageable pageable);
}
