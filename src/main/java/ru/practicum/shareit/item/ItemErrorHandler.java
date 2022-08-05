package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.IncorectUserOrItemIdException;
import ru.practicum.shareit.exceptions.IncorrectUserIdException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.NotUsedCommentException;

import java.util.Map;

@RestControllerAdvice("ru.practicum.shareit.item")
public class ItemErrorHandler {
    @ExceptionHandler(ModelNotExitsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notExitsCheck(ModelNotExitsException e) {
        return Map.of("error", e.getMessage(), e.getParam(), e.getValue());
    }

    @ExceptionHandler(IncorectUserOrItemIdException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> incorrectUserOrItemIdCheck(IncorectUserOrItemIdException e) {
        return Map.of("error", e.getMessage(), "user", String.valueOf(e.getUserId()),
                "Item", String.valueOf(e.getItemId()));
    }

    @ExceptionHandler(IncorrectUserIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> incorrectUserIdCheck(IncorrectUserIdException e) {
        return Map.of("error", e.getMessage(), "usser", e.getUserId());
    }
    @ExceptionHandler(NotUsedCommentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> notUsedComentHendle(NotUsedCommentException e){
        return Map.of("error",String.valueOf(e.getMessage()),
                "userId",String.valueOf(e.getUserId()),
                "ietemId",String.valueOf(e.getItemId()));
    }

}
