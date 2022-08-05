package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findByItem_IdOrderByCreatedDesc(long itemId);
}
