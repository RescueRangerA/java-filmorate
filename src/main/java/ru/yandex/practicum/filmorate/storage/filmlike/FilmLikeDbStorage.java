package ru.yandex.practicum.filmorate.storage.filmlike;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmLike;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmLikeDbStorage implements FilmLikeStorage {
    public static class FilmLikeMapper implements RowMapper<FilmLike> {
        @Override
        public FilmLike mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmLike(
                    (new FilmDbStorage.FilmMapper()).mapRow(rs, rowNum),
                    (new UserDbStorage.UserMapper()).mapRow(rs, rowNum)
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmLikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<FilmLike> findFilmLikesAll() {
        return jdbcTemplate.query(
                "SELECT * FROM film_like " +
                        "LEFT JOIN films ON films.id = film_like.film_id " +
                        "LEFT JOIN users ON users.id = film_like.genre_id",
                new FilmLikeMapper()
        );
    }

    @Override
    public FilmLike saveFilmLike(FilmLike entity) {
        Assert.notNull(entity, "Entity must not be null.");
        Assert.notNull(entity.getFilm().getId(), "Film id must not be null.");
        Assert.notNull(entity.getUser().getId(), "User id must not be null.");

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("INSERT INTO film_like (film_id, user_id) VALUES (?,?) ON CONFLICT DO NOTHING");
            statement.setLong(1, entity.getFilm().getId());
            statement.setLong(2, entity.getUser().getId());
            return statement;
        });

        return entity;
    }

    @Override
    public void deleteFilmLike(FilmLike entity) {
        Assert.notNull(entity, "Entity must not be null.");
        Assert.notNull(entity.getFilm().getId(), "Film id must not be null.");
        Assert.notNull(entity.getUser().getId(), "User id must not be null.");

        jdbcTemplate.update(
                "DELETE FROM film_like WHERE film_id = ? AND user_id = ?",
                entity.getFilm().getId(),
                entity.getUser().getId()
        );
    }

    @Override
    public Iterable<Film> findTop10MostLikedFilms(Integer limit) {
        if (limit < 1) {
            return List.of();
        }

        return jdbcTemplate.query(
                "SELECT films.*,film_mpa_rating.*, COUNT(film_like.film_id) as likes FROM films " +
                        "LEFT JOIN film_like ON films.id = film_like.film_id " +
                        "LEFT JOIN film_mpa_rating ON films.rating_id = film_mpa_rating.id " +
                        "GROUP BY films.id " +
                        "ORDER BY likes DESC " +
                        "LIMIT ?",
                new FilmDbStorage.FilmMapper(),
                limit
        );
    }
}
