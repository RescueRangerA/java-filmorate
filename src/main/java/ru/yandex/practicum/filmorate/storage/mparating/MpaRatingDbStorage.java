package ru.yandex.practicum.filmorate.storage.mparating;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class MpaRatingDbStorage implements MpaRatingStorage {

    public static class MpaRatingMapper implements RowMapper<FilmMpaRating> {
        @Override
        public FilmMpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmMpaRating(
                    rs.getLong("film_mpa_rating.id"),
                    rs.getString("film_mpa_rating.title"),
                    rs.getString("film_mpa_rating.description")
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<FilmMpaRating> findAll() {
        return jdbcTemplate.query(
                "SELECT film_mpa_rating.* FROM film_mpa_rating",
                new MpaRatingMapper()
        );
    }

    @Override
    public Optional<FilmMpaRating> findById(Long aLong) {
        Assert.notNull(aLong, "Rating id must not be null.");

        FilmMpaRating rating = null;

        try {
            rating = jdbcTemplate.queryForObject(
                    "SELECT film_mpa_rating.* FROM film_mpa_rating WHERE film_mpa_rating.id = ?",
                    new MpaRatingMapper(),
                    aLong
            );
        } catch (EmptyResultDataAccessException ignored) {

        }

        return rating != null ? Optional.of(rating) : Optional.empty();
    }
}
