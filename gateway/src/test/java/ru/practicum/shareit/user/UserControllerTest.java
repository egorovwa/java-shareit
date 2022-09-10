package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.contract.user.dto.UserDto;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @MockBean
    UserClient userClient;
    @Autowired
    ObjectMapper objectMapper;
    MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void test1_1_addUser() throws Exception {
        UserDto userDto = new UserDto(null, "Email@mail.com", "name");
        mvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));
        Mockito.verify(userClient, times(1)).postUser(userDto);
    }

    @Test
    void test1_2_addUser_badEmail() throws Exception {
        UserDto userDto = new UserDto(null, "Emailmail.com", "name");
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
        Mockito.verify(userClient, times(0)).postUser(userDto);
    }

    @Test
    void test2_1findById() throws Exception {
        mvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
        );
        Mockito.verify(userClient, timeout(1)).getUser(1L);
    }

    @Test
    void test2_2findById_notFound() throws Exception {
        when(userClient.getUser(1L))
                .thenReturn(ResponseEntity.notFound().build());
        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    void test3_1_patchUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("name");
        mvc.perform(patch("/users/1")
                .content(objectMapper.writeValueAsString(userDto))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));
        verify(userClient, times(1)).pathUser(1L, userDto);

    }

    @Test
    void test3_2_patchUser_badEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("vdsfgdsfgdfg");
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @Test
    void test3_3_patchUser_notFound() throws Exception {
        UserDto userDto = new UserDto();
        when(userClient.pathUser(1L, userDto))
                .thenReturn(ResponseEntity.notFound().build());
        userDto.setEmail("Email@mail.com");
        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    void test4_1_delete() throws Exception {
        mvc.perform(delete("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
        );
        Mockito.verify(userClient, timeout(1)).deleteUser(1L);
    }

    @Test
    void test4_2_delete_notFound() throws Exception {
        when(userClient.deleteUser(1L))
                .thenReturn(ResponseEntity.notFound().build());
        mvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAll() throws Exception {
        mvc.perform(get("/users")
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON));
        verify(userClient, times(1)).getAllUsers();
    }
}