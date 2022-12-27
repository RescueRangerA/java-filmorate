package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreStorage {
    public static class GenreMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(
                    rs.getLong("genre.id"),
                    rs.getString("genre.title")
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        return jdbcTemplate.query(
                "SELECT genre.* FROM genre",
                new GenreMapper()
        );
    }

    @Override
    public Optional<Genre> findById(Long genreId) {
        Assert.notNull(genreId, "Genre id must not be null.");

        Genre genre = null;

        try {
            genre = jdbcTemplate.queryForObject(
                    "SELECT genre.* FROM genre WHERE genre.id = ?",
                    new GenreMapper(),
                    genreId
            );
        } catch (EmptyResultDataAccessException ignored) {

        }

        return Optional.ofNullable(genre);
    }

    @Override
    public List<Genre> findAllById(List<Long> genreIds) {
        Assert.notNull(genreIds, "Genre ids must not be null.");

        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));

        return jdbcTemplate.query(
                String.format("SELECT * FROM genre WHERE id IN (%s)", inSql),
                new GenreMapper(),
                genreIds.toArray()
        );
    }
}
