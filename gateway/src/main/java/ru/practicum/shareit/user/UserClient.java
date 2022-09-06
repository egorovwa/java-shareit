package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoToCreate;

@Service
public class UserClient extends BaseClient {
    private static String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> postUser(UserDtoToCreate userDto) {
        return post("", userDto);
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId);

    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> pathUser(Long userId, UserDto userDto) {
        String path = "/" + userId;
        return patch(path, userDto);
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("");
    }
}
