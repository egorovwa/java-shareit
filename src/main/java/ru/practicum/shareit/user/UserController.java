package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMaper;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
private final UserServise userServise;
private final UserDtoMaper userDtoMaper;

@PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto user) throws UserBadEmailException, UserAlreadyExistsException {
   return userDtoMaper.toDto(userServise.addUser(userDtoMaper.fromDto(user)));
}
@PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable long userId, @Valid @RequestBody UserDto user) throws ModelNotExitsException, UserAlreadyExistsException {
    return userDtoMaper.toDto(userServise.updateUser(userId,userDtoMaper.fromDto(user)));
}
@DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) throws ModelNotExitsException {
    userServise.deleteUser(userId);
}
@GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) throws ModelNotExitsException {
    return userDtoMaper.toDto(userServise.findById(userId));
}
@GetMapping
    public Collection<UserDto> findAll(){
    return userServise.findAll().stream()
            .map(userDtoMaper::toDto).collect(Collectors.toList());
}
}
