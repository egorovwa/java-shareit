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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.dto.UserDtoToCreate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = ShareItGateway.class)
@ExtendWith(MockitoExtension.class)
class UserClientCacheTest {

    @Autowired
    UserClient userClient;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(userClient, "rest", restTemplate);
    }

    @Test
    void test1_createUser_duplicate() {
        UserDtoToCreate userDto = new UserDtoToCreate(null, "email@mail.com", "name");
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
    void tes2_getUser_duplicate(){
        UserDtoToCreate userDto = new UserDtoToCreate(null, "email@mail.com", "name");
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
}