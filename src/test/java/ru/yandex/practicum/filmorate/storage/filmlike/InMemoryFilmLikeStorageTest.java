package ru.yandex.practicum.filmorate.storage.filmlike;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

class InMemoryFilmLikeStorageTest {

    private InMemoryFilmLikeStorage storage;

    private InMemoryFilmStorage filmStorage;

    @BeforeEach
    void beforeEach() {
        filmStorage = new InMemoryFilmStorage();
        storage = new InMemoryFilmLikeStorage(filmStorage);
    }

    @Test
    void createWithFilmIdAndUserId() {
        Film film = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        User user = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.saveFilmLike(new FilmLike(film, user));
        Assertions.assertEquals(1, storage.findFilmLikesAll().size());
    }

    @Test
    void createWithFilmIdAndUserIdDuplicate() {
        Film film = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        User user = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.saveFilmLike(new FilmLike(film, user));
        Assertions.assertEquals(1, storage.findFilmLikesAll().size());
    }

    @Test
    void deleteByFilmIdAndUserId() {
        Film film = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        User user = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.saveFilmLike(new FilmLike(film, user));
        storage.deleteFilmLike(new FilmLike(film, user));
        Assertions.assertEquals(0, storage.findFilmLikesAll().size());
    }

    @Test
    void deleteNonExistingByFilmIdAndUserId() {
        Film film = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        User user = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertThrows(
                EntityIsNotFoundException.class,
                () -> storage.deleteFilmLike(new FilmLike(film, user))
        );
    }

    @Test()
    void getAllAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN() {
        Film film1 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film2 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film3 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film4 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film5 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film6 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film7 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film8 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film9 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);
        Film film10 = new Film(0L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, null);

        filmStorage.save(film1);
        filmStorage.save(film2);
        filmStorage.save(film3);
        filmStorage.save(film4);
        filmStorage.save(film5);
        filmStorage.save(film6);
        filmStorage.save(film7);
        filmStorage.save(film8);
        filmStorage.save(film9);
        filmStorage.save(film10);

        User user1 = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user2 = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user3 = new User(3L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));


        storage.saveFilmLike(new FilmLike(film1, user1));
        storage.saveFilmLike(new FilmLike(film2, user1));
        storage.saveFilmLike(new FilmLike(film3, user1));
        storage.saveFilmLike(new FilmLike(film4, user1));
        storage.saveFilmLike(new FilmLike(film5, user1));
        storage.saveFilmLike(new FilmLike(film6, user1));
        storage.saveFilmLike(new FilmLike(film7, user1));
        storage.saveFilmLike(new FilmLike(film8, user1));
        storage.saveFilmLike(new FilmLike(film9, user1));
        storage.saveFilmLike(new FilmLike(film10, user1));
        storage.saveFilmLike(new FilmLike(film2, user2));
        storage.saveFilmLike(new FilmLike(film4, user2));
        storage.saveFilmLike(new FilmLike(film2, user3));

        Assertions.assertEquals(
                List.of(film2, film4, film1),
                storage.findTop10MostLikedFilms(3)
        );
    }
}