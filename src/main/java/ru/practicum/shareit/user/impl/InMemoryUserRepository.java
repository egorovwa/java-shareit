package ru.practicum.shareit.user.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> userData = new HashMap<>();
    private long id = 1;

    @Override
    public Optional<User> findByEmail(String email) {

        return userData.values().stream().filter(u -> u.getEmail().equals(email)).findAny();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(id);
            id++;
        }
        userData.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(long useeId) {
        return userData.keySet().stream().filter(i -> i == useeId)
                .map(userData::get)
                .findAny();
    }

    @Override
    public void deleteById(long userId) {
        userData.remove(userId);
    }

    @Override
    public Collection<User> findAll() {
        return new ArrayList<>(userData.values());
    }

}
