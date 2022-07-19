package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.exceptions.UserNotExitsException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserServise {
    UserDto addUser(UserDto user) throws UserAlreadyExistsException, UserBadEmailException;

    UserDto updateUser(long useeId, UserDto user) throws UserNotExitsException, UserAlreadyExistsException;

    void deleteUser(long userId) throws UserNotExitsException;

    UserDto findById(long userId) throws UserNotExitsException;

    Collection<UserDto> findAll();
}
