package ru.practicum.shareit.user;

import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.exceptions.ModelCrudException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice("ru.practicum.shareit.user")
public class UserErrorHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response checkUserFiled(ValidationException e) {
        return new Response();
    }

    @ExceptionHandler(ModelAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> checkEmailAlreadyExistsException(ModelAlreadyExistsException e) {
        return Map.of("error", e.getMessage(), e.getParam(), e.getValue());
    }

    @ExceptionHandler(ModelNotExitsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> checkCurdException(ModelCrudException e) {
        return Map.of("error", e.getMessage(), e.getParam(), e.getValue());
    }

    @ExceptionHandler(UserBadEmailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> checkCurdException(UserBadEmailException e) {
        return Map.of("error", e.getMessage());
    }


}
