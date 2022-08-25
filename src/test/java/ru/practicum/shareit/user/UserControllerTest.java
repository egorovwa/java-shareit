package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.exceptions.ModelAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMaper;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @BeforeEach
    void setup(WebApplicationContext web){
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
    void test1_1_createWrongEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("sdfsdf");
        userDto.setName("name");
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

}