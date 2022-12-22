package ru.yandex.practicum.filmorate.storage.filmdirector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;

@Component
public class FilmDirectorDbStorage implements FilmDirectorStorage {

    final private JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film saveDirectorsOfTheFilm(Film film) {
        final String sqlQuery = "INSERT INTO film_director(film_id, director_id) VALUES (?,?)";

        if(film.getDirectors().size() == 0) return film;

        jdbcTemplate.batchUpdate(
                sqlQuery,
                film.getDirectors(),
                100,
                (PreparedStatement ps, Director director) -> {
                    ps.setLong(1, film.getId());
                    ps.setLong(2, director.getId());
                }
        );
        return film;
    }

    @Override
    public void deleteDirectorsFromFilm(Film film) {
        final String sqlQuery = "DELETE FROM film_director WHERE film_id = ?;";

        jdbcTemplate.update(
                sqlQuery,
                film.getId()
        );
    }
}
