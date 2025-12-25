package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Slf4j
@JdbcTest
@ComponentScan("ru.yandex.practicum.filmorate.storage")
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {

	@Autowired
	private final UserDbStorage userStorage;

	@Autowired
	private final MpaDbStorage mpaDbStorage;

	@Autowired
	private final GenreDbStorage genreDbStorage;

	@Test
	public void testFindNonExistindUserById() {
		Long id = 100L;
		Assertions.assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> userStorage.getUserById(id));
	;

	}

    @Test
	public void testCreateUser() {
		User createUser = new User();
		createUser.setEmail("some@test.ru");
		createUser.setLogin("someuser");
		User user = userStorage.create(createUser);
		Long id = user.getId();
		Assertions.assertThat(user).hasFieldOrPropertyWithValue("name", "someuser");
		Assertions.assertThat(user).hasFieldOrPropertyWithValue("login", "someuser");
		Assertions.assertThat(user).hasFieldOrPropertyWithValue("email", "some@test.ru");
	}

	@Test
	public void testUpdateUser() {
		User initialUser = new User();
		initialUser.setEmail("some@test.ru");
		initialUser.setLogin("someuser");
		initialUser = userStorage.create(initialUser);
		Long id = initialUser.getId();
		User newUser = new User();
		newUser.setId(id);
		newUser.setEmail("update@test.ru");
		newUser.setLogin("updateduser");
		User user = userStorage.update(newUser);
		Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", id);
		Assertions.assertThat(user).hasFieldOrPropertyWithValue("name", "someuser");
		Assertions.assertThat(user).hasFieldOrPropertyWithValue("login", "updateduser");
		Assertions.assertThat(user).hasFieldOrPropertyWithValue("email", "update@test.ru");
	}

	@Test
	public void testDeleteUser() {
		User initialUser = new User();
		initialUser.setEmail("some@test.ru");
		initialUser.setLogin("someuser");
		initialUser = userStorage.create(initialUser);
		Long id = initialUser.getId();
		userStorage.delete(id);
		Assertions.assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> userStorage.getUserById(id));;
	}

	@Test
	public void testAddFriend() {
		User firstUser = new User();
		firstUser.setEmail("first@test.ru");
		firstUser.setLogin("firstuser");
		firstUser = userStorage.create(firstUser);
		Long firstUserId = firstUser.getId();
		User secondUser = new User();
		secondUser.setEmail("second@test.ru");
		secondUser.setLogin("seconduser");
		secondUser = userStorage.create(secondUser);
		Long secondUserId = secondUser.getId();
		userStorage.addFriend(firstUserId, secondUserId);
		Collection<User> firstUserFriends = userStorage.getUserFriends(firstUserId);
		Collection<User> secondUserFriends = userStorage.getUserFriends(secondUserId);
		Assertions.assertThat(firstUserFriends.size()).isEqualTo(1);
		Assertions.assertThat(secondUserFriends.size()).isEqualTo(0);
		Assertions.assertThat(firstUserFriends.contains(secondUser)).isTrue();
	}

	@Test
	public void testAddMutualFriend() {
		User firstUser = new User();
		firstUser.setEmail("first@test.ru");
		firstUser.setLogin("firstuser");
		firstUser = userStorage.create(firstUser);
		Long firstUserId = firstUser.getId();
		User secondUser = new User();
		secondUser.setEmail("second@test.ru");
		secondUser.setLogin("seconduser");
		secondUser = userStorage.create(secondUser);
		Long secondUserId = secondUser.getId();
		userStorage.addFriend(firstUserId, secondUserId);
		userStorage.addFriend(secondUserId, firstUserId);
		Collection<User> firstUserFriends = userStorage.getUserFriends(firstUserId);
		Collection<User> secondUserFriends = userStorage.getUserFriends(secondUserId);
		Assertions.assertThat(firstUserFriends.size()).isEqualTo(1);
		Assertions.assertThat(secondUserFriends.size()).isEqualTo(1);
		Assertions.assertThat(firstUserFriends.contains(secondUser)).isTrue();
		Assertions.assertThat(secondUserFriends.contains(firstUser)).isTrue();
	}

	@Test
	public void testDeleteFriend() {
		User firstUser = new User();
		firstUser.setEmail("first@test.ru");
		firstUser.setLogin("firstuser");
		firstUser = userStorage.create(firstUser);
		Long firstUserId = firstUser.getId();
		User secondUser = new User();
		secondUser.setEmail("second@test.ru");
		secondUser.setLogin("seconduser");
		secondUser = userStorage.create(secondUser);
		Long secondUserId = secondUser.getId();
		userStorage.addFriend(firstUserId, secondUserId);
		userStorage.deleteFriend(firstUserId, secondUserId);
		Collection<User> firstUserFriends = userStorage.getUserFriends(firstUserId);
		Assertions.assertThat(firstUserFriends.size()).isEqualTo(0);
	}

	@Test
	public void testCommonFriend() {
		User firstUser = new User();
		firstUser.setEmail("first@test.ru");
		firstUser.setLogin("firstuser");
		firstUser = userStorage.create(firstUser);
		Long firstUserId = firstUser.getId();

		User secondUser = new User();
		secondUser.setEmail("second@test.ru");
		secondUser.setLogin("seconduser");
		secondUser = userStorage.create(secondUser);
		Long secondUserId = secondUser.getId();

		User thirdUser = new User();
		thirdUser.setEmail("third@test.ru");
		thirdUser.setLogin("thirduser");
		thirdUser = userStorage.create(thirdUser);
		Long thirdUserId = thirdUser.getId();

		userStorage.addFriend(firstUserId, thirdUserId);
		userStorage.addFriend(secondUserId, thirdUserId);
		Collection<User> commonFriends = userStorage.getCommonFriends(firstUserId, secondUserId);
		Assertions.assertThat(commonFriends.size()).isEqualTo(1);
		Assertions.assertThat(commonFriends.contains(thirdUser)).isTrue();
	}

	@Test
	public void testAllMpa() {
		Collection<Mpa> allMpa = mpaDbStorage.getAllMpa();
		Assertions.assertThat(allMpa.size()).isEqualTo(5);
	}

	@Test
	public void testMpaById() {
		Mpa firstMpa = mpaDbStorage.getMpaById(1);
		Assertions.assertThat(firstMpa).hasFieldOrPropertyWithValue("id", 1);
		Assertions.assertThat(firstMpa).hasFieldOrPropertyWithValue("name", "G");
	}

	@Test
	public void testAllGenres() {
		Collection<Genre> allGenres = genreDbStorage.getAllGenre();
		Assertions.assertThat(allGenres.size()).isEqualTo(6);
	}

	@Test
	public void testGenreById() {
		Genre firstGenre = genreDbStorage.getGenreById(1);
		Assertions.assertThat(firstGenre).hasFieldOrPropertyWithValue("id", 1);
		Assertions.assertThat(firstGenre).hasFieldOrPropertyWithValue("name", "Комедия");
	}
}
