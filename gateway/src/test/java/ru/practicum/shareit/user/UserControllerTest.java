package ru.practicum.shareit.user;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UserDtoToCreate;

@SpringBootTest(classes = ShareItGateway.class)
class UserControllerTest {
    @Autowired
    UserClient userClient;
    @Autowired
    ObjectMapper objectMapper;
    MockMvc mvc;

    @BeforeEach
    void setup(WebApplicationContext web) {
        mvc = MockMvcBuilders.webAppContextSetup(web).build();
    }

    @Test
    void addUser() {
        UserDtoToCreate userDto = new UserDtoToCreate(null, "Email@mail.com", "name");
       // mvc.perform()
    }

    @Test
    void findById() {
    }

    @Test
    void patchUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void findAll() {
    }
}