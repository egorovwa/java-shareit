package ru.practicum.shareit.user.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.Entitys.USER_ID1;
import static ru.practicum.shareit.Entitys.USER_ID2;

@ExtendWith(MockitoExtension.class)
class UserServiseImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiseImpl userServise;

    @Test
    void test1_addUser() throws ModelAlreadyExistsException {
        User normalUser = new User();
        normalUser.setEmail("mail@mail.com");
        normalUser.setName("normalUser");
        User withOutEmail = new User();
        withOutEmail.setName("name");
        User withExistEmail = new User();
        withExistEmail.setEmail("mail@mail.com");
        withExistEmail.setName("user");
        Mockito
                .when(userRepository.save(normalUser))
                .thenReturn(new User(1L, "mail@mail.com", "normalUser"));
        Mockito
                .when(userRepository.save(withExistEmail))
                .thenThrow(DataIntegrityViolationException.class);
        UserServiseImpl userServise = new UserServiseImpl(userRepository);

        assertThat(userServise.addUser(normalUser).getId(), is(1L));

        assertThrows(UserBadEmailException.class, () -> userServise.addUser(withOutEmail));

        assertThrows(ModelAlreadyExistsException.class, () -> userServise.addUser(withExistEmail));
    }

    @Test
    void test2_1_updateUserName() throws ModelNotExitsException, ModelAlreadyExistsException {
        User savedUser = USER_ID1;
        User toUpdateUser = new User();
        toUpdateUser.setName("updatedName");
        User updatedUser = USER_ID1;
        updatedUser.setName("updatedName");
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(savedUser));
        Mockito
                .when(userRepository.save(updatedUser))
                .thenReturn(updatedUser);
        UserServiseImpl userServise = new UserServiseImpl(userRepository);

        User resultUser = userServise.updateUser(1, toUpdateUser);

        assertThat(resultUser.getName(), is(toUpdateUser.getName()));

    }

    @Test
    void test2_2_updateUserEmail() throws ModelNotExitsException, ModelAlreadyExistsException {
        User savedUser = USER_ID1;
        User toUpdateUser = new User();
        toUpdateUser.setEmail("updated@email.com");
        User updatedUser = USER_ID1;
        updatedUser.setEmail("updated@email.com");
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(savedUser));
        Mockito
                .when(userRepository.save(updatedUser))
                .thenReturn(updatedUser);
        UserServiseImpl userServise = new UserServiseImpl(userRepository);

        User resultUser = userServise.updateUser(1, toUpdateUser);

        assertThat(resultUser.getEmail(), is("updated@email.com"));
    }

    @Test
    void test2_3_UpdateUserWithError() {
        User toUpdate = new User();
        toUpdate.setEmail("Mail2@mail.ru");
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.of(USER_ID1));
        Mockito
                .when(userRepository.findByEmail("Mail2@mail.ru"))
                .thenReturn(Optional.of(USER_ID2));
        UserServiseImpl userServise = new UserServiseImpl(userRepository);
        assertThrows(ModelAlreadyExistsException.class, () -> userServise.updateUser(1L, toUpdate));
    }

    @Test
    void test3_deleteUserWithWrongId() {
        Mockito
                .when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        UserServiseImpl userServise = new UserServiseImpl(userRepository);

        assertThrows(ModelNotExitsException.class, () -> userServise.deleteUser(11));
    }
    @Test
    void test3_1_deleteUser() throws ModelNotExitsException {
        Mockito
                .when(userRepository.findById(1L))
                        .thenReturn(Optional.of(USER_ID1));
        userServise.deleteUser(1L);
        Mockito
                .verify(userRepository,Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void test4_findByIdNotFound() throws ModelNotExitsException {
        Mockito
                .when(userRepository.findById(22L))
                .thenReturn(Optional.empty());
        assertThrows(ModelNotExitsException.class, () -> userServise.findById(22));
    }
    @Test
    void test5_findAll(){
        userServise.findAll();
        Mockito.verify(userRepository,Mockito.times(1)).findAll();
    }
// TODO: 24.08.2022 проверять вроде нечего
/*    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }*/
}