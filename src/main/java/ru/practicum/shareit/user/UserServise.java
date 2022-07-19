package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.UserCrudException;

import java.util.Collection;

public interface UserServise {
    User addUser(User user) throws UserCrudException;

    User updateUser(long useeId, User user) throws UserCrudException;

    void deleteUser(long userId) throws UserCrudException;

    User findById(long userId) throws UserCrudException;

    Collection<User> findAll();
}
