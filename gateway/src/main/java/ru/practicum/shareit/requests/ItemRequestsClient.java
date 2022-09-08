package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestsClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestsClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }
    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> postRequest(Long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }
    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> getRequests(String path, Long userId, Integer from, Integer size) {
        Map<String, Object> pasrameters = Map.of(
                "from", from,
                "size", size);
        return get(path + "?from={from}&size={size}", userId, pasrameters);
    }
    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> getRequests(Long userId) {
        return get("", userId);
    }
    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> getRequests(Long userId, Long itemId) {
        String path = "/" + itemId;
        return get(path, userId);
    }
}
