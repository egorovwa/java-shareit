package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoToCreate;

@Service
@Slf4j
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder, ApplicationContext context) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    @Cacheable(cacheNames = "userRequests", key = "#userDto.email", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> postUser(UserDtoToCreate userDto) {
        log.debug("create user email = {}", userDto.getEmail());
        return post("", userDto);
    }

    @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200")
    public ResponseEntity<Object> getUser(Long userId) {
        return get("/" + userId);

    }

    @Caching(
            cacheable = @Cacheable(cacheNames = "requests", unless = "#result.statusCodeValue == 200"),
            evict = @CacheEvict(cacheNames = "userRequests", key = "#userDto.email", allEntries = true,
                    condition = "#result.statusCodeValue ==200"))
    public ResponseEntity<Object> pathUser(Long userId, UserDto userDto) {
        String path = "/" + userId;
        log.debug("patchUser  = {}", userDto);
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
