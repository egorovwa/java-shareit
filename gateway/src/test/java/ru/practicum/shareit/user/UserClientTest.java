package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import ru.practicum.contract.user.dto.UserDto;
import ru.practicum.shareit.ShareItGateway;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = ShareItGateway.class)
@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Autowired
    UserClient userClient;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(userClient, "rest", restTemplate);
    }

    @Test
    @DirtiesContext
    void test1_createUser_duplicate() {
        UserDto userDto = new UserDto(null, "email@mail.com", "name");
        ResponseEntity<Object> response = new ResponseEntity<>(userDto, HttpStatus.CONFLICT);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);
        ResponseEntity<Object> out = userClient.postUser(userDto);
        out = userClient.postUser(userDto);
        out = userClient.postUser(userDto);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    @DirtiesContext
    void test2_getUser_duplicate() {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = userClient.getUser(1L);
        result = userClient.getUser(1L);
        assertThat(result, is(response));
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    @DirtiesContext
    void test3_patchUser_duplicate() {
        UserDto userDto = new UserDto(null, "email@mail.com", "name");
        ResponseEntity<Object> response = new ResponseEntity<>(userDto, HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);
        ResponseEntity<Object> out = userClient.pathUser(1L, userDto);
        out = userClient.pathUser(1L, userDto);
        out = userClient.pathUser(1L, userDto);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    @DirtiesContext
    void test4_delete_duplicate() {
        UserDto userDto = new UserDto(null, "email@mail.com", "name");
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);
        userClient.deleteUser(1L);
        userClient.deleteUser(1L);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    @DirtiesContext
    void test5_getAll_duplicate() {
        UserDto userDto = new UserDto(null, "email@mail.com", "name");
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);
        userClient.getAllUsers();
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    @DirtiesContext
    void test6_creatAftePatch() {
        UserDto userDto = new UserDto(null, "email@mail.com", "name");
        ResponseEntity<Object> response = new ResponseEntity<>(userDto, HttpStatus.CONFLICT);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);
        ResponseEntity<Object> out = userClient.postUser(userDto);
        UserDto patchDto = new UserDto(1L, "updated@email.com", null);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Object> resonsePatch = userClient.pathUser(1L, patchDto);
        userClient.postUser(userDto);
        Mockito.verify(restTemplate, Mockito.times(3)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    @DirtiesContext
    void test6_1creatAftePatchName() {
        UserDto userDto = new UserDto(null, "email@mail.com", "name");
        ResponseEntity<Object> response = new ResponseEntity<>(userDto, HttpStatus.CONFLICT);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);
        ResponseEntity<Object> out = userClient.postUser(userDto);
        UserDto patchDto = new UserDto(1L, null, "update");
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Object> resonsePatch = userClient.pathUser(1L, patchDto);
        userClient.postUser(userDto);
        Mockito.verify(restTemplate, Mockito.times(3)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }
}