package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.InMemoryFilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class FilmControllerTest {
    private FilmController controller;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @BeforeEach
    void beforeEach() {
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryFilmLikeStorage(), new InMemoryUserStorage()));
    }

    @Test
    void create() {
        Film film = new Film(null, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, 1L);

        Assertions.assertEquals(Set.of(), validator.validate(film));
        Film createdFilm = controller.create(film);

        Assertions.assertEquals(1L, createdFilm.getId());
        assertEqualsFilms(film, createdFilm);
    }

    @Test
    void validateFailName() {
        Film film = new Film(null, "", "Description", LocalDate.parse("1900-03-25", DateTimeFormatter.ISO_DATE), 200, 1L);

        Assertions.assertEquals(1, validator.validate(film).size());
    }

    @Test
    void validateFailDescription() {
        Film film = new Film(null, "Film name", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.", LocalDate.parse("1900-03-25", DateTimeFormatter.ISO_DATE), 200, 1L);

        Assertions.assertEquals(1, validator.validate(film).size());
    }

    @Test
    void validateFailReleaseDate() {
        Film film = new Film(null, "Name", "Description", LocalDate.parse("1890-03-25", DateTimeFormatter.ISO_DATE), 200, 1L);

        Assertions.assertEquals(1, validator.validate(film).size());
    }

    @Test
    void validateFailDuration() {
        Film film = new Film(null, "Name", "Description", LocalDate.parse("1900-03-25", DateTimeFormatter.ISO_DATE), -200, 1L);

        Assertions.assertEquals(1, validator.validate(film).size());
    }

    @Test
    void update() {
        Film film = new Film(null, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, 1L);

        Assertions.assertEquals(Set.of(), validator.validate(film));
        Film createdFilm = controller.create(film);

        Assertions.assertEquals(1, createdFilm.getId());
        assertEqualsFilms(film, createdFilm);

        Film updateFilm = new Film(1L, "Film Updated", "New film update decription", LocalDate.parse("1989-04-17", DateTimeFormatter.ISO_DATE), 190, 1L);
        Assertions.assertEquals(Set.of(), validator.validate(film));

        AtomicReference<Film> updatedFilm = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> updatedFilm.set(controller.createOrUpdate(updateFilm)));
        Assertions.assertEquals(updateFilm, updatedFilm.get());
    }

    @Test
    void updateUnknown() {
        Film film = new Film(null, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100, 1L);

        Assertions.assertEquals(Set.of(), validator.validate(film));
        Film createdFilm = controller.create(film);

        Assertions.assertEquals(1, createdFilm.getId());
        assertEqualsFilms(film, createdFilm);

        Film updateFilm = new Film(9999L, "Film Updated", "New film update decription", LocalDate.parse("1989-04-17", DateTimeFormatter.ISO_DATE), 190, 1L);
        Assertions.assertEquals(Set.of(), validator.validate(film));

        Assertions.assertThrows(EntityIsNotFoundException.class, () -> controller.createOrUpdate(updateFilm));
    }

    void assertEqualsFilms(Film filmExpected, Film filmActual) {
        Assertions.assertEquals(filmExpected.getName(), filmActual.getName());
        Assertions.assertEquals(filmExpected.getDescription(), filmActual.getDescription());
        Assertions.assertEquals(filmExpected.getReleaseDate(), filmActual.getReleaseDate());
        Assertions.assertEquals(filmExpected.getDuration(), filmActual.getDuration());
    }
}
