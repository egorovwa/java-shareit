package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.exceptions.ModelNotExitsException;
import ru.practicum.shareit.exceptions.UserBadEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMaper;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(UserDtoMaper.class)
class UserControllerTest {
    String GOOD_EMAIL = "email@email.com";
    @MockBean
    UserServise userServise;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mvc;

    UserDtoMaper dtoMaper = new UserDtoMaper();

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void test1_createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail(GOOD_EMAIL);
        userDto.setName("name");
        User user = new User();
        user.setEmail(GOOD_EMAIL);
        user.setName("name");
        User usersaved = new User();
        usersaved.setEmail(GOOD_EMAIL);
        usersaved.setName("name");
        usersaved.setId(1L);
        when(userServise.addUser(user))
                .thenReturn(usersaved);
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is(GOOD_EMAIL)));
    }

    @Test
    void test1_2_createUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        when(userServise.addUser(dtoMaper.fromDto(userDto)))
                .thenThrow(new UserBadEmailException("message"));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(UserBadEmailException.class)));

    }

    @Test
    void test1_1_createWrongEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("sdfsdf");
        userDto.setName("name");
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResolvedException().getClass(),
                        is(MethodArgumentNotValidException.class)));
    }

    @Test
    void test1_1_createAlradiExist() throws Exception {
        User user = new User();
        user.setEmail(GOOD_EMAIL);
        user.setName("name");
        when(userServise.addUser(user))
                .thenThrow(new ModelAlreadyExistsException("message", "param", "val"));
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(dtoMaper.toDto(user)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
                        ModelAlreadyExistsException));
    }

    @Test
    void test2_delete_notFound() throws Exception {
        Mockito.doThrow(new ModelNotExitsException("message", "id", "val"))
                .when(userServise).deleteUser(1L);
        mvc.perform(delete("/users/{userid}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
                        ModelNotExitsException));
    }

    @Test
    void test2_1delete() throws Exception {
        mvc.perform(delete("/users/{userid}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userServise, Mockito.times(1)).deleteUser(1L);
    }

    @Test
    void test3_patchUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail(GOOD_EMAIL);
        userDto.setName("name");
        when(userServise.updateUser(1L, dtoMaper.fromDto(userDto)))
                .thenReturn(dtoMaper.fromDto(userDto));
        mvc.perform(patch("/users/{userid}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
        Mockito.verify(userServise, Mockito.times(1))
                .updateUser(1L, dtoMaper.fromDto(userDto));
    }

    @Test
    void test4_getUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail(GOOD_EMAIL);
        userDto.setName("name");
        when(userServise.findById(1L))
                .thenReturn(dtoMaper.fromDto(userDto));
        mvc.perform(get("/users/{userid}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userServise, Mockito.times(1)).findById(1L);
    }

    @Test
    void test5_getUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail(GOOD_EMAIL);
        userDto.setName("name");
        when(userServise.findAll())
                .thenReturn(List.of(dtoMaper.fromDto(userDto)));
        mvc.perform(get("/users/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Mockito.verify(userServise, Mockito.times(1)).findAll();
    }


}
