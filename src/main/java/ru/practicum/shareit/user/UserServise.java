package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserServise {
    UserDto addUser(UserDto user) throws UserAlreadyExistsException, UserBadEmailException;

    UserDto updateUser(long useeId, UserDto user) throws ModelNotExitsException, UserAlreadyExistsException;

    void deleteUser(long userId) throws ModelNotExitsException;

    UserDto findByIdDto(long userId) throws ModelNotExitsException;
    User findById(long userId) throws ModelNotExitsException;


    Collection<UserDto> findAll();
}
