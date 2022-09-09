package ru.practicum.shareit.requests;

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
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = ShareItGateway.class)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestsClientTest {
    @Autowired
    ItemRequestsClient client;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(client, "rest", restTemplate);
    }

    @Test
    void test1_postRequest() {
        ItemRequestDto dto = new ItemRequestDto(null, "ddddddd", null);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(ResponseEntity.status(404).build());
        client.postRequest(1L, dto);
        client.postRequest(1L, dto);
        client.postRequest(1L, dto);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void test2_GetRequests() {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class), any(Map.class)))
                .thenReturn(response);
        client.getRequests("", 1L, 0, 3);
        client.getRequests("", 1L, 0, 3);
        client.getRequests("", 1L, 0, 3);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class));
    }

    @Test
    void test3_getRequests() {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = client.getRequests(1L);
        result = client.getRequests(1L);
        assertThat(result, is(response));
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void testGetRequests1() {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);
        client.getRequests(1L, 1L);
        client.getRequests(1L, 1L);
        client.getRequests(1L, 1L);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }
}