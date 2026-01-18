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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

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

    @Autowired
    private final FilmDbStorage filmDbStorage;

    @Test
    public void testFindNonExistingUserById() {
        Long id = 100L;
        Assertions.assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> userStorage.getUserById(id));
    }

    @Test
    public void testCreateUser() {
        int initialSize = userStorage.getAllUsers().size();
        User createUser = new User();
        createUser.setEmail("some@test.ru");
        createUser.setLogin("someuser");
        User user = userStorage.create(createUser);
        Assertions.assertThat(userStorage.getAllUsers().size()).isEqualTo(initialSize + 1);

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
        Assertions.assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> userStorage.getUserById(id));
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

    @Test
    public void testFindNonExistingFilmById() {
        Long id = 100L;
        Assertions.assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> filmDbStorage.getFilmById(id));
    }

    @Test
    public void testCreateFilm() {
        int initialSize = filmDbStorage.getAllFilms().size();
        Film createFilm = new Film();
        createFilm.setName("Some Film");
        Film film = filmDbStorage.create(createFilm);
        Assertions.assertThat(filmDbStorage.getAllFilms().size()).isEqualTo(initialSize + 1);
    }

    @Test
    public void testUpdateFilm() {
        Film initialFilm = new Film();
        initialFilm.setName("Some Film");
        initialFilm = filmDbStorage.create(initialFilm);
        Long id = initialFilm.getId();

        Film newFilm = new Film();
        newFilm.setId(id);
        newFilm.setName("Updated Film");
        Film film = filmDbStorage.update(newFilm);
        Assertions.assertThat(film).hasFieldOrPropertyWithValue("id", id);
        Assertions.assertThat(film).hasFieldOrPropertyWithValue("name", "Updated Film");
    }

    @Test
    public void testDeleteFilm() {
        Film initialFilm = new Film();
        initialFilm.setName("Some Film");
        initialFilm = filmDbStorage.create(initialFilm);
        Long id = initialFilm.getId();
        filmDbStorage.delete(id);
        Assertions.assertThatExceptionOfType(NotFoundException.class).isThrownBy(() -> filmDbStorage.getFilmById(id));
    }

    @Test
    public void testLikeFilm() {
        Film initialFilm = new Film();
        initialFilm.setName("Some Film");
        initialFilm = filmDbStorage.create(initialFilm);
        Long filmId = initialFilm.getId();

        User firstUser = new User();
        firstUser.setEmail("first@test.ru");
        firstUser.setLogin("firstuser");
        firstUser = userStorage.create(firstUser);
        Long firstUserId = firstUser.getId();

        filmDbStorage.like(filmId, firstUserId);
        Assertions.assertThat(filmDbStorage.getFilmById(filmId)).hasFieldOrPropertyWithValue("likesCnt", 1);
    }

    @Test
    public void testUnlikeFilm() {
        Film initialFilm = new Film();
        initialFilm.setName("Some Film");
        initialFilm = filmDbStorage.create(initialFilm);
        Long filmId = initialFilm.getId();

        User firstUser = new User();
        firstUser.setEmail("first@test.ru");
        firstUser.setLogin("firstuser");
        firstUser = userStorage.create(firstUser);
        Long firstUserId = firstUser.getId();

        filmDbStorage.like(filmId, firstUserId);
        filmDbStorage.unlike(filmId, firstUserId);
        Assertions.assertThat(filmDbStorage.getFilmById(filmId)).hasFieldOrPropertyWithValue("likesCnt", 0);
    }

//    @Test
//    public void testTopFilm() {
//        Film firstFilm = new Film();
//        firstFilm.setName("First Film");
//        firstFilm = filmDbStorage.create(firstFilm);
//        Long firstFilmId = firstFilm.getId();
//
//        Film secondFilm = new Film();
//        secondFilm.setName("Second Film");
//        secondFilm = filmDbStorage.create(secondFilm);
//        Long secondFilmId = secondFilm.getId();
//
//        User firstUser = new User();
//        firstUser.setEmail("first@test.ru");
//        firstUser.setLogin("firstuser");
//        firstUser = userStorage.create(firstUser);
//        Long firstUserId = firstUser.getId();
//
//        User secondUser = new User();
//        secondUser.setEmail("second@test.ru");
//        secondUser.setLogin("seconduser");
//        secondUser = userStorage.create(secondUser);
//        Long secondUserId = secondUser.getId();
//
//        filmDbStorage.like(firstFilmId, firstUserId);
//        filmDbStorage.like(firstFilmId, secondUserId);
//        filmDbStorage.like(secondFilmId, firstUserId);
//
//        firstFilm = filmDbStorage.getFilmById(firstFilmId);
//        secondFilm = filmDbStorage.getFilmById(secondFilmId);
//        Collection<Film> topFilms = filmDbStorage.getTopFilms(2);
//        Assertions.assertThat(topFilms.size()).isEqualTo(2);
//        Assertions.assertThat(topFilms.toArray()[0]).isEqualTo(firstFilm);
//        Assertions.assertThat(topFilms.toArray()[1]).isEqualTo(secondFilm);
//    }

    @Test
    public void testFindMostSimilarUser() {
        User user1 = new User();
        user1.setEmail("user1@test.ru");
        user1.setLogin("user1");
        user1 = userStorage.create(user1);
        Long user1Id = user1.getId();

        User user2 = new User();
        user2.setEmail("user2@test.ru");
        user2.setLogin("user2");
        user2 = userStorage.create(user2);
        Long user2Id = user2.getId();

        User user3 = new User();
        user3.setEmail("user3@test.ru");
        user3.setLogin("user3");
        user3 = userStorage.create(user3);
        Long user3Id = user3.getId();

        Film film1 = new Film();
        film1.setName("Film 1");
        film1 = filmDbStorage.create(film1);
        Long film1Id = film1.getId();

        Film film2 = new Film();
        film2.setName("Film 2");
        film2 = filmDbStorage.create(film2);
        Long film2Id = film2.getId();

        Film film3 = new Film();
        film3.setName("Film 3");
        film3 = filmDbStorage.create(film3);
        Long film3Id = film3.getId();

        filmDbStorage.like(film1Id, user1Id);
        filmDbStorage.like(film2Id, user1Id);

        filmDbStorage.like(film1Id, user2Id);
        filmDbStorage.like(film2Id, user2Id);
        filmDbStorage.like(film3Id, user2Id);

        filmDbStorage.like(film1Id, user3Id);

        Long similarUserForUser1 = userStorage.findMostSimilarUser(user1Id);
        Assertions.assertThat(similarUserForUser1).isEqualTo(user2Id);

        Long similarUserForUser3 = userStorage.findMostSimilarUser(user3Id);
        Assertions.assertThat(similarUserForUser3).isIn(user1Id, user2Id);
    }

    @Test
    public void testFindMostSimilarUserWhenNoCommonLikes() {
        User user1 = new User();
        user1.setEmail("user1@test.ru");
        user1.setLogin("user1");
        user1 = userStorage.create(user1);
        Long user1Id = user1.getId();

        User user2 = new User();
        user2.setEmail("user2@test.ru");
        user2.setLogin("user2");
        user2 = userStorage.create(user2);
        Long user2Id = user2.getId();

        Long similarUser = userStorage.findMostSimilarUser(user1Id);
        Assertions.assertThat(similarUser).isNull();
    }

    @Test
    public void testGetFilmsLikedByUserButNotByOther() {
        User user1 = new User();
        user1.setEmail("user1@test.ru");
        user1.setLogin("user1");
        user1 = userStorage.create(user1);
        Long user1Id = user1.getId();

        User user2 = new User();
        user2.setEmail("user2@test.ru");
        user2.setLogin("user2");
        user2 = userStorage.create(user2);
        Long user2Id = user2.getId();

        Film film1 = new Film();
        film1.setName("Film 1");
        film1 = filmDbStorage.create(film1);
        Long film1Id = film1.getId();

        Film film2 = new Film();
        film2.setName("Film 2");
        film2 = filmDbStorage.create(film2);
        Long film2Id = film2.getId();

        Film film3 = new Film();
        film3.setName("Film 3");
        film3 = filmDbStorage.create(film3);
        Long film3Id = film3.getId();

        filmDbStorage.like(film1Id, user1Id);
        filmDbStorage.like(film2Id, user1Id);

        filmDbStorage.like(film2Id, user2Id);
        filmDbStorage.like(film3Id, user2Id);

        List<Film> filmsLikedByUser1ButNotByUser2 = filmDbStorage.getFilmsLikedByUserButNotByOther(user1Id, user2Id);
        Assertions.assertThat(filmsLikedByUser1ButNotByUser2).hasSize(1);
        Assertions.assertThat(filmsLikedByUser1ButNotByUser2.get(0).getId()).isEqualTo(film1Id);

        List<Film> filmsLikedByUser2ButNotByUser1 = filmDbStorage.getFilmsLikedByUserButNotByOther(user2Id, user1Id);
        Assertions.assertThat(filmsLikedByUser2ButNotByUser1).hasSize(1);
        Assertions.assertThat(filmsLikedByUser2ButNotByUser1.get(0).getId()).isEqualTo(film3Id);
    }

    @Test
    public void testGetFilmsLikedByUserButNotByOtherWhenNoFilms() {
        User user1 = new User();
        user1.setEmail("user1@test.ru");
        user1.setLogin("user1");
        user1 = userStorage.create(user1);
        Long user1Id = user1.getId();

        User user2 = new User();
        user2.setEmail("user2@test.ru");
        user2.setLogin("user2");
        user2 = userStorage.create(user2);
        Long user2Id = user2.getId();

        Film film1 = new Film();
        film1.setName("Film 1");
        film1 = filmDbStorage.create(film1);
        Long film1Id = film1.getId();

        filmDbStorage.like(film1Id, user1Id);
        filmDbStorage.like(film1Id, user2Id);

        List<Film> films = filmDbStorage.getFilmsLikedByUserButNotByOther(user1Id, user2Id);
        Assertions.assertThat(films).isEmpty();
    }

    @Test
    public void testGetPopularFilmsBasic() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1 = filmDbStorage.create(film1);

        Film film2 = new Film();
        film2.setName("Film 2");

        User user1 = new User();
        user1.setEmail("user1@test.ru");
        user1.setLogin("user1");
        user1 = userStorage.create(user1);

        filmDbStorage.like(film1.getId(), user1.getId());

        List<Film> films = filmDbStorage.getPopularFilms(1, null, null);
        Assertions.assertThat(films).hasSize(1);
        Assertions.assertThat(films.get(0).getId()).isEqualTo(film1.getId());
    }

    @Test
    public void testGetPopularFilmsAllParams() {
        Film film1 = new Film();
        film1.setName("Film 1");
        film1 = filmDbStorage.create(film1);

        User user1 = new User();
        user1.setEmail("user1@test.ru");
        user1.setLogin("user1");
        user1 = userStorage.create(user1);

        filmDbStorage.like(film1.getId(), user1.getId());

        List<Film> films = filmDbStorage.getPopularFilms(5, null, null);
        Assertions.assertThat(films).isNotEmpty();
    }

}
