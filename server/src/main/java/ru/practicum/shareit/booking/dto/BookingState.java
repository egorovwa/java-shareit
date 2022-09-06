package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
    ALL,
    CURRENT, // (англ. «текущие»),
    PAST, // (англ. «завершённые»),
    FUTURE, // (англ. «будущие»),
    WAITING, // (англ. «ожидающие подтверждения»),
    REJECTED; // (англ. «отклонённые»).
    public static Optional<BookingState> from(String stringState) {
        for (BookingState state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
