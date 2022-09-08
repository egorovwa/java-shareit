package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
@SpringBootTest(classes = ShareItGateway.class)
@ExtendWith(MockitoExtension.class)
class BookingClientTest {
    @Autowired
    BookingClient client;
    @Mock
    RestTemplate restTemplate;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(client, "rest", restTemplate);
    }

    @Test
    void test1_getBookings() {
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class), any(Map.class)))
                .thenReturn(ResponseEntity.notFound().build());
        client.getBookings(1L, BookingState.CURRENT, 0, 3);
        client.getBookings(1L, BookingState.CURRENT, 0, 3);
        client.getBookings(1L, BookingState.CURRENT, 0, 3);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class));
    }

    @Test
    void test2_bookItem() {
        BookItemRequestDto dto = new BookItemRequestDto();

        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(ResponseEntity.notFound().build());
        client.bookItem(1L, dto);
        client.bookItem(1L, dto);
        client.bookItem(1L, dto);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));

    }

    @Test
    void test3_getBooking() {
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class)))
                .thenReturn(ResponseEntity.notFound().build());
        client.getBookings(1L, 1L);
        client.getBookings(1L, 1L);
        client.getBookings(1L, 1L);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void patchBooking() {
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class), any(Map.class)))
                .thenReturn(ResponseEntity.notFound().build());
        client.patchBooking(1L,1L, true);
        client.patchBooking(1L,1L, true);
        client.patchBooking(1L,1L, true);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class));
    }

    @Test
    void testGetBooking() {
        Mockito.when(restTemplate.exchange(Mockito.anyString(), any(HttpMethod.class), any(HttpEntity.class),
                        any(Class.class), any(Map.class)))
                .thenReturn(ResponseEntity.notFound().build());
        client.getBookings("patt",1L, BookingState.CURRENT,0,3);
        client.getBookings("patt",1L, BookingState.CURRENT,0,3);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(Mockito.anyString(),
                any(HttpMethod.class), any(HttpEntity.class), any(Class.class), any(Map.class));
    }
}