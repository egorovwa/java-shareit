package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL,
    CURRENT, // (англ. «текущие»),
    PAST, // (англ. «завершённые»),
    FUTURE, // (англ. «будущие»),
    WAITING, // (англ. «ожидающие подтверждения»),
    REJECTED // (англ. «отклонённые»).
}
