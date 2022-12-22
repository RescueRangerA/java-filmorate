package ru.yandex.practicum.filmorate.storage.filmgenre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmGenreDirector;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film saveGenresOfTheFilm(Film filmEntity) {
        if (filmEntity.getGenres().size() > 0) {
            Assert.notNull(filmEntity.getId(), "Film id must not be null.");

            jdbcTemplate.batchUpdate(
                    "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)",
                    filmEntity.getGenres(),
                    100,
                    (PreparedStatement ps, Genre genre) -> {
                        Assert.notNull(genre.getId(), "Genre id must not be null.");

                        ps.setLong(1, filmEntity.getId());
                        ps.setLong(2, genre.getId());
                    }
            );
        }

        return filmEntity;
    }

    @Override
    public void deleteAllGenresOfTheFilm(Film filmEntity) {
        Assert.notNull(filmEntity, "Entity must not be null.");

        jdbcTemplate.update(
                "DELETE FROM film_genre WHERE film_id = ?",
                filmEntity.getId()
        );
    }

    @Override
    public List<FilmGenreDirector> findFilmGenresOfTheFilms(List<Film> filmEntities) {
        List<Long> filmIds = filmEntities.stream().map(Film::getId).collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        return jdbcTemplate.query(
                String.format(
                        "SELECT film.*, film_mpa_rating.*, genre.*, director.* FROM film " +
                                "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                                "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                                "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                                "LEFT JOIN film_director ON film_director.film_id = film.id " +
                                "LEFT JOIN director ON film_director.director_id = director.id " +
                                "WHERE film.id IN (%s)", inSql),
                new FilmDbStorage.FilmGenreDirectorMapper(),
                filmIds.toArray()
        );
    }
}
