package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;

/**
 * // TODO .
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
private final UserServise userServise;

@PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto user) throws UserBadEmailException, UserAlreadyExistsException {
   return userServise.addUser(user);
}
@PatchMapping("/{userId}")
    public UserDto patchUser(@PathVariable long userId, @Valid @RequestBody UserDto user) throws ModelNotExitsException, UserAlreadyExistsException {
    return userServise.updateUser(userId,user);
}
@DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) throws ModelNotExitsException {
    userServise.deleteUser(userId);
}
@GetMapping("/{userId}")
    public UserDto findById(@PathVariable long userId) throws ModelNotExitsException {
    return userServise.findByIdDto(userId);
}
@GetMapping
    public Collection<UserDto> findAll(){
    return userServise.findAll();
}
}
