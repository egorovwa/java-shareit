package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServise;

@SpringBootTest
@RequiredArgsConstructor
class ShareItTests {
	private final UserServise userServise;
	User user1 = new User(null,"user1@nfff.com","user1");
	User user2 = new User(null,"user2@nfff.com","user2");
	@Test
	void contextLoads() {
	}
	@Test
	void test1_adduser_findById_Normal() throws UserAlreadyExistsException {
		userServise.addUser(user1);
	}

}
