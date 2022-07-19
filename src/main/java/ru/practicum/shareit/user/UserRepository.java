package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<User> findById(long useeId);

    void deleteById(long userId);

    Collection<User> findAll();

}
