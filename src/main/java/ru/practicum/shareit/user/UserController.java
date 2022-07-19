package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exceptions.UserCrudException;

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
    public User addUser(@Valid @RequestBody User user) throws UserCrudException {
   return userServise.addUser(user);
}
@PatchMapping("/{userId}")
    public User patchUser(@PathVariable long userId, @Valid @RequestBody User user) throws UserCrudException {
    return userServise.updateUser(userId,user);
}
@DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) throws UserCrudException {
    userServise.deleteUser(userId);
}
@GetMapping("/{userId}")
    public User findById(@PathVariable long userId) throws UserCrudException {
    return userServise.findById(userId);
}
@GetMapping
    public Collection<User> findAll(){
    return userServise.findAll();
}
}
