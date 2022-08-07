package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiseTest {
    private final UserServise userServise;
    final User user2 = new User(null, "email2@mail.ru", "user");
    final User user3 = new User(null, "email3@mail.ru", "user");
    private final User user = new User(null, "email@mail.ru", "user");

    @Test
    @DirtiesContext
    void test1_addUser() throws ModelAlreadyExistsException, ModelNotExitsException {
        userServise.addUser(user);
        User findedUser = userServise.findById(1);
        assertEquals(user.getName(), findedUser.getName(), "имя не совпадает");
        assertEquals(user.getEmail(), findedUser.getEmail(), "email не совпадает");
    }

    @Test
    @DirtiesContext
    void test2_addWithError() throws ModelAlreadyExistsException {
        userServise.addUser(user);
        assertThrows(ModelAlreadyExistsException.class, () -> {
            User userWithDoubleEmail = new User(null, "email@mail.ru", "user1");
            userServise.addUser(userWithDoubleEmail);
        });
    }

    @Test
    @DirtiesContext
    void test3_updateUser() throws ModelAlreadyExistsException, ModelNotExitsException {
        User userUpdate = new User(null, "updated@mail.ru", "updatedName");
        userServise.addUser(user);
        userServise.updateUser(1, userUpdate);

        assertEquals(userUpdate.getEmail(), userServise.findById(1).getEmail(), "email не совпадает");
        assertEquals(userUpdate.getName(), userServise.findById(1).getName(), "name не совпадает");
    }

    @Test
    @DirtiesContext
    void test4_updateNoExistUser() {
        assertThrows(ModelNotExitsException.class, () -> userServise.updateUser(2, user2));
    }

    @Test
    @DirtiesContext
    void test5_deleteUser() throws ModelNotExitsException, ModelAlreadyExistsException {
        userServise.addUser(user);

        userServise.deleteUser(1);
        assertEquals(0, userServise.findAll().size());
    }

    @Test
    @DirtiesContext
    void test5_1_deleteUserwith3() throws ModelAlreadyExistsException, ModelNotExitsException {
        userServise.addUser(user);
        userServise.addUser(user2);
        userServise.addUser(user3);
        Collection<User> all = userServise.findAll();
        userServise.deleteUser(2);
        Collection<User> del = userServise.findAll();
        assertEquals(2, userServise.findAll().size());
        assertTrue(userServise.findAll().contains(user));
    }

    @Test
    @DirtiesContext
    void test6_deleteNotExitsUser() throws ModelAlreadyExistsException {
        userServise.addUser(user);
        userServise.addUser(user2);
        userServise.addUser(user3);

        assertThrows(ModelNotExitsException.class, () -> userServise.deleteUser(5));
    }

    @Test
    @DirtiesContext
    void test7_findById() throws ModelAlreadyExistsException, ModelNotExitsException {
        userServise.addUser(user);
        userServise.addUser(user2);
        userServise.addUser(user3);

        assertEquals(user.getName(), userServise.findById(1).getName());
    }

    @Test
    @DirtiesContext
    void test8_findNotExistUser() throws ModelAlreadyExistsException {
        userServise.addUser(user);
        userServise.addUser(user2);
        userServise.addUser(user3);
        assertThrows(ModelNotExitsException.class, () -> userServise.findById(7));
    }

    @Test
    @DirtiesContext
    void test9_findAll() throws ModelAlreadyExistsException {
        userServise.addUser(user);
        userServise.addUser(user2);
        userServise.addUser(user3);
        assertEquals(3, userServise.findAll().size());
    }
}