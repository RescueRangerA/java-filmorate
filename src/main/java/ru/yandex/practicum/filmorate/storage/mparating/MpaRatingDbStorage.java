package ru.yandex.practicum.filmorate.storage.mparating;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
                "SELECT * FROM film_mpa_rating",
                new MpaRatingMapper()
        );
    }

    @Override
    public Optional<FilmMpaRating> findById(Long aLong) {
        FilmMpaRating genre = null;

        try {
            genre = jdbcTemplate.queryForObject(
                    "SELECT * FROM film_mpa_rating WHERE film_mpa_rating.id = ?",
                    new MpaRatingMapper(),
                    aLong
            );
        } catch (EmptyResultDataAccessException ignored) {

        }

        return genre != null ? Optional.of(genre) : Optional.empty();
    }

    @Override
    public Iterable<FilmMpaRating> findAllById(Iterable<Long> longs) {
        List<Long> ids = StreamSupport
                .stream(longs.spliterator(), false)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM film_mpa_rating WHERE id IN (%s)", inSql),
                new MpaRatingMapper(),
                ids.toArray()
        );
    }
}
