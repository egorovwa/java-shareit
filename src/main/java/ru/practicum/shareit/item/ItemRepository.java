package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Collection<Item> findByOwnerIdOrderByIdAsc(long userId);

    @Query("SELECT i FROM Item i WHERE (upper(i.name) LIKE upper(concat('%',:text,'%')) OR " +
            "upper(i.description) LIKE upper(concat('%',:text,'%')) AND i.available = true )")
    Collection<Item> findByText(@Param("text") String text);
}
