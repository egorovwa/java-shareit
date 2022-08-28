package ru.practicum.shareit.requests;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.IncorrectPageValueException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;

import java.util.Map;

@RestControllerAdvice("ru.practicum.shareit.requests")
public class ItemRequestErrorHendler {
    @ExceptionHandler(ModelNotExitsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> userNotFoundExceptionHendler(ModelNotExitsException e) {
        return Map.of("Error", e.getMessage(), e.getParam(), e.getValue());
    }
    @ExceptionHandler(IncorrectPageValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> incorrectPageValue(IncorrectPageValueException e){
        return Map.of("error", e.getMessage());
    }
}
