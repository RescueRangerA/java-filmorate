package ru.yandex.practicum.filmorate.storage.filmlike;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

class InMemoryFilmLikeStorageTest {

    private InMemoryFilmLikeStorage storage;

    @BeforeEach
    void beforeEach() {
        storage = new InMemoryFilmLikeStorage();
    }

    @Test
    void createWithFilmIdAndUserId() throws EntityAlreadyExistsException {
        Film film = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        User user = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.createWithFilmIdAndUserId(film, user);
        Assertions.assertEquals(1, storage.getAll().size());
    }

    @Test
    void createWithFilmIdAndUserIdDuplicate() throws EntityAlreadyExistsException {
        Film film = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        User user = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.createWithFilmIdAndUserId(film, user);
        Assertions.assertEquals(1, storage.getAll().size());

        Assertions.assertThrows(
                EntityAlreadyExistsException.class,
                () -> storage.createWithFilmIdAndUserId(film, user)
        );
    }

    @Test
    void deleteByFilmIdAndUserId() throws EntityAlreadyExistsException, EntityIsNotFoundException {
        Film film = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        User user = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        storage.createWithFilmIdAndUserId(film, user);
        storage.deleteByFilmIdAndUserId(film, user);
        Assertions.assertEquals(0, storage.getAll().size());
    }

    @Test
    void deleteNonExistingByFilmIdAndUserId() {
        Film film = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        User user = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        Assertions.assertThrows(
                EntityIsNotFoundException.class,
                () -> storage.deleteByFilmIdAndUserId(film, user)
        );
    }

    @Test()
    void getAllAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN() throws EntityAlreadyExistsException {
        Film film1 = new Film(1L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film2 = new Film(2L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film3 = new Film(3L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film4 = new Film(4L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film5 = new Film(5L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film6 = new Film(6L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film7 = new Film(7L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film8 = new Film(8L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film9 = new Film(9L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);
        Film film10 = new Film(10L, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);

        User user1 = new User(1L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user2 = new User(2L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));
        User user3 = new User(3L, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));


        storage.createWithFilmIdAndUserId(film1, user1);
        storage.createWithFilmIdAndUserId(film2, user1);
        storage.createWithFilmIdAndUserId(film3, user1);
        storage.createWithFilmIdAndUserId(film4, user1);
        storage.createWithFilmIdAndUserId(film5, user1);
        storage.createWithFilmIdAndUserId(film6, user1);
        storage.createWithFilmIdAndUserId(film7, user1);
        storage.createWithFilmIdAndUserId(film8, user1);
        storage.createWithFilmIdAndUserId(film9, user1);
        storage.createWithFilmIdAndUserId(film10, user1);
        storage.createWithFilmIdAndUserId(film2, user2);
        storage.createWithFilmIdAndUserId(film4, user2);
        storage.createWithFilmIdAndUserId(film2, user3);

        Assertions.assertEquals(
                List.of(2L, 4L, 1L),
                storage.getFilmIdsAndGroupByFilmIdWithCountSumAndOrderByCountSumDescAndLimitN(3)
        );
    }
}