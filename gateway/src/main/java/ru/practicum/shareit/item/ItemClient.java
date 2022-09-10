package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.contract.client.BaseClient;
import ru.practicum.contract.item.dto.CommentDto;
import ru.practicum.contract.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> postItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> pacthItem(Long userId, Long itemId, ItemDto itemDto) {
        String path = "/" + itemId;
        return patch(path, userId, itemDto);
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        String path = "/" + itemId;
        return get(path, userId);
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> getItems(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> getItems(String path, String text, Integer size, Integer from, long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "size", size,
                "from", from);
        return get(path + "?text={text}&from={from}&size={size}", userId, parameters);
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> postComment(String path, long userId, CommentDto text) {
        return post(path, userId, text);
    }
}
