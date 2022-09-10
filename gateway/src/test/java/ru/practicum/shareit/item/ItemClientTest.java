package ru.practicum.shareit.item;

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
import ru.practicum.contract.item.dto.CommentDto;
import ru.practicum.contract.item.dto.ItemDto;
import ru.practicum.shareit.ShareItGateway;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = ShareItGateway.class)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemClientTest {
    @Autowired
    ItemClient client;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(client, "rest", restTemplate);
    }

    @Test
    void test1_postItem() {
        ItemDto dto = new ItemDto(null, "itemName", "dddddd", true, 1L);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(ResponseEntity.status(404).build());
        client.postItem(1L, dto);
        client.postItem(1L, dto);
        client.postItem(1L, dto);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }


    @Test
    void test2_pacthItem() {
        ItemDto dto = new ItemDto(1L, "itemName", "dddddd", true, 1L);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(ResponseEntity.status(404).build());
        client.pacthItem(1L, 1L, dto);
        client.pacthItem(1L, 1L, dto);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void test3_getItem() {
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(ResponseEntity.status(404).build());
        client.getItem(1L, 1L);
        client.getItem(1L, 1L);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }


    @Test
    void test4_getItems() {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class), any(Map.class)))
                .thenReturn(response);
        client.getItems(1, 0, 3);
        client.getItems(1, 0, 3);
        client.getItems(1, 0, 3);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class));
    }

    @Test
    void test5_GetItems() {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class), any(Map.class)))
                .thenReturn(response);
        client.getItems("path", "text", 0, 3, 1L);
        client.getItems("path", "text", 0, 3, 1L);
        client.getItems("path", "text", 0, 3, 1L);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class));
    }

    @Test
    void test6_postComment() {
        CommentDto dto = new CommentDto();
        dto.setText("text");
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(response);
        client.postComment("path", 1L, dto);
        client.postComment("path", 1L, dto);
        client.postComment("path", 1L, dto);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }
}