package ru.yandex.practicum.filmorate.storage.filmgenre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    public static class FilmGenreMapper implements RowMapper<FilmGenre> {
        @Override
        public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmGenre(
                    (new FilmDbStorage.FilmMapper()).mapRow(rs, rowNum),
                    (new GenreDbStorage.GenreMapper()).mapRow(rs, rowNum)
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<FilmGenre> findFilmGenreAll() {
        return jdbcTemplate.query(
                "SELECT * FROM film_genre " +
                        "LEFT JOIN films ON films.id = film_genre.film_id " +
                        "LEFT JOIN genres ON genres.id = film_genre.genre_id",
                new FilmGenreMapper()
        );
    }

    @Override
    public Iterable<FilmGenre> findAllByFilm(Film film) {
        List<Genre> genreList = jdbcTemplate.query(
                "SELECT * FROM genres " +
                        "LEFT JOIN film_genre ON genres.id = film_genre.genre_id " +
                        "LEFT JOIN films ON films.id = film_genre.film_id " +
                        "WHERE film_genre.film_id = ?",
                new GenreDbStorage.GenreMapper(),
                film.getId()
        );

        return genreList.stream().map(genre -> new FilmGenre(film, genre)).collect(Collectors.toList());
    }

    @Override
    public FilmGenre saveFilmGenre(FilmGenre entity) {
        Assert.notNull(entity, "Entity must not be null.");
        Assert.notNull(entity.getFilm().getId(), "Film id must not be null.");
        Assert.notNull(entity.getGenre().getId(), "Genre id must not be null.");

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("INSERT INTO film_genre (film_id, genre_id) VALUES (?,?) ON CONFLICT DO NOTHING");
            statement.setLong(1, entity.getFilm().getId());
            statement.setLong(2, entity.getGenre().getId());
            return statement;
        });

        return entity;
    }

    @Override
    public void deleteFilmGenre(FilmGenre entity) {
        Assert.notNull(entity, "Entity must not be null.");
        Assert.notNull(entity.getFilm().getId(), "Film id must not be null.");
        Assert.notNull(entity.getGenre().getId(), "Genre id must not be null.");

        jdbcTemplate.update(
                "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?",
                entity.getFilm().getId(),
                entity.getGenre().getId()
        );
    }

    @Override
    public void deleteAllByFilm(Film film) {
        Assert.notNull(film, "Entity must not be null.");

        jdbcTemplate.update(
                "DELETE FROM film_genre WHERE film_id = ?",
                film.getId()
        );
    }
}
