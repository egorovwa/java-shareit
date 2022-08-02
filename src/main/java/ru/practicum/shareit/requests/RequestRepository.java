package ru.practicum.shareit.requests;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<ItemRequest,Long> {
}
