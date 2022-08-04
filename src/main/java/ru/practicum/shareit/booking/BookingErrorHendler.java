package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;

import java.util.Map;

@RestControllerAdvice("ru.practicum.shareit.booking")
public class BookingErrorHendler {
    @ExceptionHandler(ModelNotExitsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> incorrectUserId(ModelNotExitsException e) {
        return Map.of("error", e.getMessage(), e.getParam(), e.getValue());
    }

    @ExceptionHandler(TimeIntersectionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> incorrectTimException(TimeIntersectionException e) {
        return Map.of("error", e.getMessage(), "Start Time", e.getStartTime().toString(),
                "End Time", e.getEndTime().toString());
    }

    @ExceptionHandler({ItemNotAvalibleExxeption.class,StatusAlredyException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> itemNotAvalible(ItemNotAvalibleExxeption e) {
        return Map.of("Error", e.getMessage(), "itemId", e.getId());
    }

    @ExceptionHandler(UserNotFoundExteption.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> usrNotFoundHendler(UserNotFoundExteption e) {
        return Map.of("error", e.getMessage(), e.getParam(), e.getValue());
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> unknownStatusHendle(UnknownStateException e) {
        return Map.of("error", "Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler(IncorrectUserIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> itemNotAvalible(IncorrectUserIdException e) {
        return Map.of("Error", e.getMessage(), "user id", e.getUserId());
    }

@ExceptionHandler(ParametrNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> parametrNotFoudHendle(ParametrNotFoundException e){
        return Map.of(e.getMessage(), e.getParam());
}

}
