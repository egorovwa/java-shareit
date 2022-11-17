package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;

import java.util.Collection;

public interface UserServise {
    User addUser(User user) throws ModelAlreadyExistsException, UserBadEmailException;

    User updateUser(long useeId, User user) throws ModelNotExitsException, ModelAlreadyExistsException;

    void deleteUser(long userId) throws ModelNotExitsException;

    User findById(long userId) throws ModelNotExitsException;


    Collection<User> findAll();
}
