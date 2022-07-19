package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserCrudException;
import ru.practicum.shareit.exceptions.UserNotExitsException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServise;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiseImpl implements UserServise {
    private final UserRepository userRepository;

    @Override
    public User addUser(User user) throws UserCrudException {

        if (userRepository.findByEmail(user.getEmail()).isEmpty() && Strings.isNotBlank(user.getEmail())) {
            log.info("Добавлен новый пользователь Email: {}", user.getEmail());
            return userRepository.save(user);
        } else {
            throw new UserCrudException("Ползователь с таким Email уже существует", "email", user.getEmail());
        }
    }

    @Override
    public User updateUser(long useeId, User user) throws UserCrudException {
        User updatedUser = userRepository.findById(useeId)
                .orElseThrow(() -> new UserNotExitsException("Ползователь с таким id не существует",
                        "id", String.valueOf(user.getId())));
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (userRepository.findByEmail(user.getEmail()).isEmpty()
                || userRepository.findByEmail(user.getEmail()).get().getEmail().equals(updatedUser.getEmail())) {
            updatedUser.setEmail(user.getEmail());
        } else {
            throw new UserAlreadyExistsException("Ползователь с таким Email уже существует", "email", user.getEmail());
        }
        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(long userId) throws UserCrudException {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            log.info("Удален  пользователь  id: {}", userId);

        } else {
            throw new UserNotExitsException("Ползователь с таким id не существует", "id", String.valueOf(userId));
        }
    }

    @Override
    public User findById(long userId) throws UserCrudException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExitsException("Пользователь не найден", "id", String.valueOf(userId)));
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }
}
