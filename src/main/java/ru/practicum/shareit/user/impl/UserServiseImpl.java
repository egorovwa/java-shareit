package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServise;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiseImpl implements UserServise {
    private final UserRepository userRepository;

    @Override
    public User addUser(User user) throws UserAlreadyExistsException, UserBadEmailException {
        if (user.getEmail() != null) {
            if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
                log.info("Добавлен новый пользователь Email: {}", user.getEmail());
                return userRepository.save(user);
            } else {
                throw new UserAlreadyExistsException("Ползователь с таким Email уже существует", "email", user.getEmail());
            }
        } else {
            throw new UserBadEmailException("Email Не может быть пустым"); // TODO: 19.07.2022 иключение сделать
        }
    }

    @Override
    public User updateUser(long useeId, User user) throws ModelNotExitsException, UserAlreadyExistsException {
        User updatedUser = userRepository.findById(useeId)
                .orElseThrow(() -> new ModelNotExitsException("Ползователь с таким id не существует",
                        "id", String.valueOf(user.getId())));
        if (userRepository.findByEmail(user.getEmail()).isEmpty()
                || userRepository.findByEmail(user.getEmail()).get().getEmail().equals(updatedUser.getEmail())) {
            if (Strings.isNotBlank(user.getName())){
                updatedUser.setName(user.getName());
            }
            if (Strings.isNotBlank(user.getEmail())){
                updatedUser.setEmail(user.getEmail());
            }
            return userRepository.save(updatedUser); // TODO: 20.07.2022 где сохраняю?
        } else {
            throw new UserAlreadyExistsException("Ползователь с таким Email уже существует", "email", user.getEmail());
        }

    }

    @Override
    public void deleteUser(long userId) throws ModelNotExitsException {
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            log.info("Удален  пользователь  id: {}", userId);

        } else {
            throw new ModelNotExitsException("Ползователь с таким id не существует", "id", String.valueOf(userId));
        }
    }

    @Override
    public User findById(long userId) throws ModelNotExitsException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotExitsException("Пользователь не найден", "id", String.valueOf(userId)));
    }

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(userRepository.findAll()); // TODO: 20.07.2022 imunable лучше
    }
}
