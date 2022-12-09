package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class GenreDbStorage implements GenreStorage {
    public static class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(
                    rs.getLong("genres.id"),
                    rs.getString("genres.title")
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Iterable<Genre> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM genres",
                new GenreMapper()
        );
    }

    @Override
    public Optional<Genre> findById(Long aLong) {
        Genre genre = null;

        try {
            genre = jdbcTemplate.queryForObject(
                    "SELECT * FROM genres WHERE genres.id = ?",
                    new GenreMapper(),
                    aLong
            );
        } catch (EmptyResultDataAccessException ignored) {

        }

        return genre != null ? Optional.of(genre) : Optional.empty();
    }

    @Override
    public Iterable<Genre> findAllById(Iterable<Long> longs) {
        List<Long> ids = StreamSupport
                .stream(longs.spliterator(), false)
                .collect(Collectors.toList());
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM genres WHERE id IN (%s)", inSql),
                new GenreMapper(),
                ids.toArray()
        );
    }
}
