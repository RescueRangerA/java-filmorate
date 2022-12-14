package ru.yandex.practicum.filmorate.storage.filmgenre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;

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
}
