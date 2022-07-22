package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private Long id;
    private Long start;
    private Long end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
