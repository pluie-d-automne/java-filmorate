package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

@JdbcTest
@ComponentScan("ru.yandex.practicum.filmorate.storage")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

	@Autowired
	private final UserDbStorage userStorage;


    @Test
	public void testCreateAndFindUserById() {
		Long id = 1L;
		User createUser = new User();
		createUser.setEmail("some@test.ru");
		createUser.setLogin("someuser");
		User user = userStorage.create(createUser);
		Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", id);
	}
}
