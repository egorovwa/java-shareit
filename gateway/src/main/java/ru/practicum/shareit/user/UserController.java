package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.contract.user.dto.UserDto;
import ru.practicum.contract.ValidationMarker;


import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({ValidationMarker.OnCreate.class})
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        log.info("Creating User {}", userDto);
        return userClient.postUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable long userId) {
        log.info("Get user id= {}", userId);
        return userClient.getUser(userId);
    }

    @PatchMapping("/{userId}")
    @Validated(ValidationMarker.OnPatch.class)
    public ResponseEntity<Object> patchUser(@PathVariable long userId, @Valid @RequestBody UserDto userDto) {
        log.info("Patch user id = {}, inData = {}",userId,userDto);
        return userClient.pathUser(userId, userDto);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.info("Delete user id = {}", userId);
        return userClient.deleteUser(userId);
    }


    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }
}
