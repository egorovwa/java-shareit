package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServise;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMaper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiseImpl implements UserServise {
    private final UserRepository userRepository;
    private final UserDtoMaper userDtoMaper;

    @Override
    public UserDto addUser(UserDto user) throws UserAlreadyExistsException, UserBadEmailException {
        if (user.getEmail() != null) {
            if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
                log.info("Добавлен новый пользователь Email: {}", user.getEmail());
                return userDtoMaper.toDto(userRepository.save(userDtoMaper.fromDto(user)));
            } else {
                throw new UserAlreadyExistsException("Ползователь с таким Email уже существует", "email", user.getEmail());
            }
        } else {
            throw new UserBadEmailException("Email Не может быть пустым"); // TODO: 19.07.2022 иключение сделать
        }
    }

    @Override
    public UserDto updateUser(long useeId, UserDto userDto) throws ModelNotExitsException, UserAlreadyExistsException {
        User updatedUser = userRepository.findById(useeId)
                .orElseThrow(() -> new ModelNotExitsException("Ползователь с таким id не существует",
                        "id", String.valueOf(userDto.getId())));
        if (userRepository.findByEmail(userDto.getEmail()).isEmpty()
                || userRepository.findByEmail(userDto.getEmail()).get().getEmail().equals(updatedUser.getEmail())) {
            return userDtoMaper.toDto(userDtoMaper.update(updatedUser, userDto));
        } else {
            throw new UserAlreadyExistsException("Ползователь с таким Email уже существует", "email", userDto.getEmail());
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
    public UserDto findByIdDto(long userId) throws ModelNotExitsException {
        return userDtoMaper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotExitsException("Пользователь не найден", "id", String.valueOf(userId))));
    }
    @Override
    public User findById(long userId) throws ModelNotExitsException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotExitsException("Пользователь не найден", "id", String.valueOf(userId)));
    }

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll().stream().map(userDtoMaper::toDto).collect(Collectors.toList());
    }
}
