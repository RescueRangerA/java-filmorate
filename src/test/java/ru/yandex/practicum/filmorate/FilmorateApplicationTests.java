package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {

    private final FilmDbStorage filmDbStorage;

    private final UserDbStorage userDbStorage;

    @Test
    @Order(1)
    public void testFindFilmsAll() {
        List<Film> films = (List<Film>) filmDbStorage.findFilmsAll();

        assertThat(films).hasSize(0);
    }

    @Test
    @Order(2)
    public void testCreateFilm() {
        Film film = filmDbStorage.saveFilm(
                new Film(
                        null,
                        "nisi eiusmod",
                        "adipisicing",
                        LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE),
                        100,
                        new FilmMpaRating(1L, null, null),
                        Set.of(
                                new Genre(1L, null),
                                new Genre(2L, null)
                        )
                )
        );

        assertThat(film).isNotNull();

        Optional<Film> filmOptional = filmDbStorage.findFilmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(filmObj ->
                        assertThat(filmObj)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "nisi eiusmod")
                                .hasFieldOrPropertyWithValue("description", "adipisicing")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE))
                                .hasFieldOrPropertyWithValue("duration", 100)
                ).hasValueSatisfying(filmObj ->
                        assertThat(filmObj.getMpa())
                                .hasFieldOrPropertyWithValue("id", 1L)
                ).hasValueSatisfying(filmObj ->
                        assertThat(filmObj.getGenres())
                                .hasSize(2)
                );
    }

    @Test
    @Order(3)
    public void testUpdateFilm() {
        Film film = filmDbStorage.saveFilm(
                new Film(
                        1L,
                        "Film Updated",
                        "New film update decription",
                        LocalDate.parse("1989-04-17", DateTimeFormatter.ISO_DATE),
                        190,
                        new FilmMpaRating(2L, null, null),
                        Set.of(
                                new Genre(3L, null)
                        )
                )
        );

        assertThat(film).isNotNull();

        Optional<Film> filmOptional = filmDbStorage.findFilmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(filmObj ->
                        assertThat(filmObj)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "Film Updated")
                                .hasFieldOrPropertyWithValue("description", "New film update decription")
                                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("1989-04-17", DateTimeFormatter.ISO_DATE))
                                .hasFieldOrPropertyWithValue("duration", 190)
                ).hasValueSatisfying(filmObj ->
                        assertThat(filmObj.getMpa())
                                .hasFieldOrPropertyWithValue("id", 2L)
                ).hasValueSatisfying(filmObj ->
                        assertThat(filmObj.getGenres())
                                .hasSize(1)
                );
    }

    @Test
    @Order(4)
    public void testUpdateFilmNoGenres() {
        Film film = filmDbStorage.saveFilm(
                new Film(
                        1L,
                        "Film Updated",
                        "New film update decription",
                        LocalDate.parse("1989-04-17", DateTimeFormatter.ISO_DATE),
                        190,
                        new FilmMpaRating(2L, null, null),
                        Set.of()
                )
        );

        assertThat(film).isNotNull();

        Optional<Film> filmOptional = filmDbStorage.findFilmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(filmObj ->
                        assertThat(filmObj.getGenres())
                                .hasSize(0)
                );
    }

    @Test
    @Order(5)
    public void testUpdateFilmUnknown() {
        assertThatThrownBy(() -> filmDbStorage.saveFilm(
                new Film(
                        9999L,
                        "Film Updated",
                        "New film update decription",
                        LocalDate.parse("1989-04-17", DateTimeFormatter.ISO_DATE),
                        190,
                        new FilmMpaRating(2L, null, null)
                )
        )).isInstanceOf(EntityIsNotFoundException.class);

        Optional<Film> filmOptional = filmDbStorage.findFilmById(9999L);
        assertThat(filmOptional).isNotPresent();
    }

    @Test
    @Order(6)
    public void testGetPopularFilms() {
        assertThat(filmDbStorage.findTopNMostLikedFilms(10))
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    @Order(7)
    public void testCreateUser() {
        User user = userDbStorage.save(
                new User(
                        null,
                        "mail@mail.ru",
                        "dolore",
                        "Nick Name",
                        LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE)
                )
        );

        assertThat(user).isNotNull();

        Optional<User> userOptional = userDbStorage.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userObj ->
                        assertThat(userObj)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "mail@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "dolore")
                                .hasFieldOrPropertyWithValue("name", "Nick Name")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE))
                );
    }

    @Test
    @Order(8)
    public void testUpdateUser() {
        User user = userDbStorage.save(
                new User(
                        1L,
                        "mail@yandex.ru",
                        "doloreUpdate",
                        "est adipisicing",
                        LocalDate.parse("1976-09-20", DateTimeFormatter.ISO_DATE)
                )
        );

        assertThat(user).isNotNull();

        Optional<User> userOptional = userDbStorage.findById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userObj ->
                        assertThat(userObj)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("email", "mail@yandex.ru")
                                .hasFieldOrPropertyWithValue("login", "doloreUpdate")
                                .hasFieldOrPropertyWithValue("name", "est adipisicing")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1976-09-20", DateTimeFormatter.ISO_DATE))
                );
    }

    @Test
    @Order(8)
    public void testGetFriendEmpty() {
        assertThat(userDbStorage.findFriendsOfUser(new User(1L, null, null, null, null)))
                .hasSize(0);
    }

    @Test
    @Order(9)
    public void testCreateFriend() {
        User user = userDbStorage.save(
                new User(
                        null,
                        "friend@mail.ru",
                        "friend",
                        "friend adipisicing",
                        LocalDate.parse("1976-08-20", DateTimeFormatter.ISO_DATE)
                )
        );

        assertThat(user).isNotNull();

        Optional<User> userOptional = userDbStorage.findById(2L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userObj ->
                        assertThat(userObj)
                                .hasFieldOrPropertyWithValue("id", 2L)
                                .hasFieldOrPropertyWithValue("email", "friend@mail.ru")
                                .hasFieldOrPropertyWithValue("login", "friend")
                                .hasFieldOrPropertyWithValue("name", "friend adipisicing")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1976-08-20", DateTimeFormatter.ISO_DATE))
                );
    }

    @Test
    @Order(10)
    public void testFriend() {
        userDbStorage.saveUserFriend(
                new UserFriend(
                        new User(1L, null, null, null, null),
                        new User(2L, null, null, null, null)
                )
        );

        assertThat(userDbStorage.findFriendsOfUser(new User(1L, null, null, null, null)))
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("email", "friend@mail.ru")
                .hasFieldOrPropertyWithValue("login", "friend")
                .hasFieldOrPropertyWithValue("name", "friend adipisicing")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1976-08-20", DateTimeFormatter.ISO_DATE));

        assertThat(userDbStorage.findFriendsOfUser(new User(2L, null, null, null, null)))
                .hasSize(0);
    }

    @Test
    @Order(11)
    public void testCommonFriends() {
        User user = userDbStorage.save(
                new User(
                        null,
                        "friend@common.ru",
                        "common",
                        "",
                        LocalDate.parse("2000-08-20", DateTimeFormatter.ISO_DATE)
                )
        );

        assertThat(user).isNotNull();

        Optional<User> userOptional = userDbStorage.findById(3L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(userObj ->
                        assertThat(userObj)
                                .hasFieldOrPropertyWithValue("id", 3L)
                                .hasFieldOrPropertyWithValue("email", "friend@common.ru")
                                .hasFieldOrPropertyWithValue("login", "common")
                                .hasFieldOrPropertyWithValue("name", "common")
                                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("2000-08-20", DateTimeFormatter.ISO_DATE))
                );

        userDbStorage.saveUserFriend(
                new UserFriend(
                        new User(3L, null, null, null, null),
                        new User(2L, null, null, null, null)
                )
        );

        assertThat(
                userDbStorage.findFriendsInCommonOf2Users(
                        new User(1L, null, null, null, null),
                        new User(3L, null, null, null, null)
                )
        ).first()
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("email", "friend@mail.ru")
                .hasFieldOrPropertyWithValue("login", "friend")
                .hasFieldOrPropertyWithValue("name", "friend adipisicing")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("1976-08-20", DateTimeFormatter.ISO_DATE));
    }
}
