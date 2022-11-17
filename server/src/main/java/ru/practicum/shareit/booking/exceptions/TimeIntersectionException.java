package ru.practicum.shareit.booking.exceptions;

import java.time.LocalDateTime;

public class TimeIntersectionException extends Exception {
    final LocalDateTime startTime;
    final LocalDateTime endTime;

    public TimeIntersectionException(String message, LocalDateTime startTime, LocalDateTime endTime) {
        super(message);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
