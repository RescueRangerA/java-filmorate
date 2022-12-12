package ru.yandex.practicum.filmorate.storage.filmgenre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film saveGenresOfTheFilm(Film film) {
        if (film.getGenres().size() > 0) {
            Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
            genres.addAll(film.getGenres());
            film.setGenres(genres);

            try {
                DataSource ds = jdbcTemplate.getDataSource();
                Assert.notNull(ds);
                Connection connection = ds.getConnection();
                connection.setAutoCommit(false);

                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO film_genre (film_id, genre_id) VALUES (?,?)"
                );

                for (Genre genre : film.getGenres()) {
                    if (film.getId() == null || genre.getId() == null) {
                        continue;
                    }

                    ps.setLong(1, film.getId());
                    ps.setLong(2, genre.getId());
                    ps.addBatch();
                }

                ps.executeBatch();
                ps.clearBatch();
                connection.commit();
                ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        return film;
    }

    @Override
    public void deleteAllGenresOfTheFilm(Film film) {
        Assert.notNull(film, "Entity must not be null.");

        jdbcTemplate.update(
                "DELETE FROM film_genre WHERE film_id = ?",
                film.getId()
        );
    }
}
