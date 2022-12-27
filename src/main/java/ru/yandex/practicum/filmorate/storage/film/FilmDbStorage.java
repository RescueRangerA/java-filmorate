package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mparating.MpaRatingDbStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilmDbStorage implements FilmStorage {
    public static class FilmMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Date releaseDate = rs.getDate("film.release_date");

            return new Film(
                    rs.getLong("film.id"),
                    rs.getString("film.title"),
                    rs.getString("film.description"),
                    releaseDate != null ? releaseDate.toLocalDate() : null,
                    rs.getInt("film.duration"),
                    new MpaRatingDbStorage.MpaRatingMapper().mapRow(rs, rowNum)
            );
        }
    }

    public static class FilmGenreMapper implements RowMapper<FilmGenre> {
        @Override
        public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmGenre(
                    new FilmMapper().mapRow(rs, rowNum),
                    new GenreDbStorage.GenreMapper().mapRow(rs, rowNum)
            );
        }
    }

    public static class FilmGenreDirectorMapper implements RowMapper<FilmGenreDirector> {
        @Override
        public FilmGenreDirector mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new FilmGenreDirector(
                    new FilmMapper().mapRow(rs, rowNum),
                    new GenreDbStorage.GenreMapper().mapRow(rs, rowNum),
                    new DirectorDbStorage.DirectorMapper().mapRow(rs, rowNum)
            );
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findFilmsAll() {
        List<Film> films = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.* FROM film " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id ",
                new FilmMapper()
        );

        return completeExternalEntitiesForFilms(films);
    }

    @Override
    public Film saveFilm(Film filmEntity) {
        Assert.notNull(filmEntity, "Film must not be null.");

        if (filmEntity.getId() == null || filmEntity.getId() == 0L) {
            GeneratedKeyHolder holder = new GeneratedKeyHolder();
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("INSERT INTO film (title, description, release_date, duration, rating_id) VALUES (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, filmEntity.getName());
                statement.setString(2, filmEntity.getDescription());
                statement.setDate(3, Date.valueOf(filmEntity.getReleaseDate()));
                statement.setInt(4, filmEntity.getDuration());
                statement.setLong(5, filmEntity.getMpa().getId() != null ? filmEntity.getMpa().getId() : 0L);
                return statement;
            }, holder);


            // https://stackoverflow.com/a/32361613
            Long newId = null;
            if (holder.getKeys() != null && holder.getKeys().size() > 1) {
                newId = (Long) holder.getKeys().get("id");
            } else if (holder.getKey() != null) {
                newId = holder.getKey().longValue();
            }

            if (newId == null) {
                throw new RuntimeException("Cannot insert the film");
            }

            filmEntity.setId(newId);
        } else {
            int rowsAffected = jdbcTemplate.update(
                    "UPDATE film SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?",
                    filmEntity.getName(),
                    filmEntity.getDescription(),
                    filmEntity.getReleaseDate(),
                    filmEntity.getDuration(),
                    filmEntity.getMpa().getId(),
                    filmEntity.getId()
            );

            if (rowsAffected == 0) {
                throw new EntityIsNotFoundException(Film.class, filmEntity.getId());
            }
        }

        return filmEntity;
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        Assert.notNull(filmId, "Film id must not be null.");

        List<FilmGenreDirector> filmsWithGenres = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.*, genre.*, director.* FROM film " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                        "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                        "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                        "LEFT JOIN film_director ON film_director.film_id = film.id " +
                        "LEFT JOIN director ON film_director.director_id = director.id " +
                        "WHERE film.id = ?",
                new FilmGenreDirectorMapper(),
                filmId
        );


        Film film = null;
        for (FilmGenreDirector filmGenre : filmsWithGenres) {
            if (film == null) {
                film = filmGenre.getFilm();
            }

            Genre genre = filmGenre.getGenre();
            if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                film.addGenre(genre);
            }

            final Director director = filmGenre.getDirector();
            if (director != null && director.getId() != null && director.getId() != 0L) {
                film.addDirector(director);
            }
        }

        return film != null ? Optional.of(film) : Optional.empty();
    }

    @Override
    public List<Film> findFilmsAllById(List<Long> filmIds) {
        Assert.notNull(filmIds, "Film ids must not be null.");

        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        List<Film> films = jdbcTemplate.query(
                String.format(
                        "SELECT film.*, film_mpa_rating.* FROM film " +
                                "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                                "WHERE film.id IN (%s)", inSql),
                new FilmMapper(),
                filmIds.toArray()
        );

        return completeExternalEntitiesForFilms(films);
    }

    @Override
    public void deleteFilmById(Long filmId) {
        Assert.notNull(filmId, "Film id must not be null.");

        jdbcTemplate.update("DELETE FROM film WHERE id = ?", filmId);
    }

    @Override
    public FilmLike saveFilmLike(FilmLike filmLikeEntity) {
        Assert.notNull(filmLikeEntity, "Entity must not be null.");
        Assert.notNull(filmLikeEntity.getFilm().getId(), "Film id must not be null.");
        Assert.notNull(filmLikeEntity.getUser().getId(), "User id must not be null.");

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement statement = con.prepareStatement("INSERT INTO film_like (film_id, user_id) VALUES (?,?)");
                statement.setLong(1, filmLikeEntity.getFilm().getId());
                statement.setLong(2, filmLikeEntity.getUser().getId());
                return statement;
            });
        } catch ( DuplicateKeyException ignore ) {

        }

        return filmLikeEntity;
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
    public List<Film> findTopNMostLikedFilms(Integer limit) {
        Assert.notNull(limit, "Limit must not be null.");

        if (limit < 1) {
            return Collections.emptyList();
        }

        return jdbcTemplate.query(
                "SELECT film.*,film_mpa_rating.*, COUNT(film_like.film_id) as likes FROM film " +
                        "LEFT JOIN film_like ON film.id = film_like.film_id " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                        "GROUP BY film.id " +
                        "ORDER BY likes DESC " +
                        "LIMIT ?",
                new FilmDbStorage.FilmMapper(),
                limit
        );
    }

    @Override
    public List<Film> searchByFilm(String query) {
        List<Film> films = jdbcTemplate.query(
            "SELECT film.*, film_mpa_rating.*, COUNT(film_like.film_id) as likes " +
                "FROM film " +
                "LEFT JOIN film_like ON film.id = film_like.film_id " +
                "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                "WHERE LOWER(film.title) LIKE ? " +
                "GROUP BY film_like.film_id " +
                "ORDER BY likes DESC",
                new FilmMapper(),
                "%" + query.toLowerCase() + "%"
        );

        return completeExternalEntitiesForFilms(films);
    }

    @Override
    public List<Film> searchByDirector(String query) {
        List<Film> films = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.*, COUNT(film_like.film_id) as likes " +
                    "FROM film " +
                    "LEFT JOIN film_like ON film.id = film_like.film_id " +
                    "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                    "LEFT JOIN film_director ON film.id = film_director.film_id " +
                    "LEFT JOIN director ON director.id = film_director.director_id " +
                    "WHERE LOWER(director.name) LIKE ? " +
                    "GROUP BY film_like.film_id " +
                    "ORDER BY likes DESC",
                new FilmMapper(),
                "%" + query.toLowerCase() + "%"
        );

        return completeExternalEntitiesForFilms(films);
    }

    @Override
    public List<Film> getFilmsFriends(Long userEntityA, Long userEntityB) {
        Assert.notNull(userEntityA, "User must not be null.");
        Assert.notNull(userEntityB, "User must not be null.");

        List<Film> films = jdbcTemplate.query(
                "SELECT f.*, fr.*, COUNT(f.id) as f_id FROM film as f " +
                "LEFT JOIN film_like as fl ON f.id = fl.film_id " +
                "LEFT JOIN film_mpa_rating as fr ON f.rating_id = fr.id " +
                "WHERE fl.user_id IN (?, ?) " +
                "GROUP BY f.id " +
                "HAVING COUNT(f.id) = 2 " +
                "ORDER BY f_id DESC",
                new FilmDbStorage.FilmMapper(),
                userEntityA, userEntityB);

        return completeExternalEntitiesForFilms(films);
    }

    @Override
    public List<Film> getFilmByDirector(final Long directorId, final String sortBy) {
        Assert.notNull(directorId, "Director id must not be null.");

        String sqlQuery = "";
        if(sortBy.equals("year")) {
            sqlQuery = "SELECT film.*, film_mpa_rating.* FROM film " +
                    "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                    "LEFT JOIN film_director ON film.id = film_director.film_id " +
                    "LEFT JOIN director ON director.id = film_director.director_id " +
                    "WHERE director.id = ? " +
                    "ORDER BY extract(year from CAST(release_date as DATE))";

        } else if(sortBy.equals("likes")) {
            sqlQuery = "SELECT film.*, film_mpa_rating.* FROM film " +
                    "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                    "LEFT JOIN film_like ON film.id = film_like.film_id " +
                    "LEFT JOIN film_director ON film.id = film_director.film_id " +
                    "LEFT JOIN director ON director.id = film_director.director_id " +
                    "WHERE director.id = ? " +
                    "GROUP BY film.id, film_mpa_rating.id " +
                    "ORDER BY COUNT(film_like.film_id)";
        }

        final List<Film> films = jdbcTemplate.query(
                sqlQuery,
                new FilmMapper(),
                directorId
        );

        return completeExternalEntitiesForFilms(films);
    }

    @Override
    public List<Film> findTopNMostLikedFilmsForGenreAndYear(Integer limit, Long genreId, Integer year) {
        Assert.notNull(limit, "Limit must not be null.");

        if (limit < 1) {
            return Collections.emptyList();
        }

        final List<Film> films = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.* FROM film " +
                        "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                        "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                        "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                        "LEFT JOIN film_like ON film.id = film_like.film_id " +
                        "WHERE (YEAR(film.release_date) = ? AND genre.id = ?) OR " +
                        "(? IS NULL AND genre.id = ?) OR " +
                        "(YEAR(film.release_date) = ? AND ? IS NULL) " +
                        "GROUP BY film.id, film_mpa_rating.id " +
                        "ORDER BY COUNT(film_like.film_id) " +
                        "LIMIT ?",
                new FilmMapper(),
                year,
                genreId,
                year,
                genreId,
                year,
                genreId,
                limit
        );

        return completeExternalEntitiesForFilms(films);
    }

    @Override
    public List<Film> searchByFilmAndDirector(String query) {
        List<Film> films = jdbcTemplate.query(
                "SELECT film.*, film_mpa_rating.*, COUNT(film_like.film_id) as likes " +
                    "FROM film " +
                    "LEFT JOIN film_like ON film.id = film_like.film_id " +
                    "LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id " +
                    "LEFT JOIN film_director ON film.id = film_director.film_id " +
                    "LEFT JOIN director ON director.id = film_director.director_id " +
                    "WHERE LOWER(film.title) LIKE ? OR LOWER(director.name) LIKE ? " +
                    "GROUP BY film_like.film_id " +
                    "ORDER BY likes DESC",
                new FilmMapper(),
                "%" + query.toLowerCase() + "%",
                "%" + query.toLowerCase() + "%"
        );

        return completeExternalEntitiesForFilms(films);
    }

    private List<Film> completeExternalEntitiesForFilms(List<Film> films) {
        Map<Long, LinkedHashSet<Genre>> genresMap = buildGenresMap(films);
        Map<Long, LinkedHashSet<Director>> directorsMap = buildDirectorsMap(films);

        for (Film film : films) {
            if (genresMap.containsKey(film.getId())) {
                film.setGenres(genresMap.get(film.getId()));
            }
            if (directorsMap.containsKey(film.getId())) {
                film.setDirectors(directorsMap.get(film.getId()));
            }
        }

        return films;
    }

    private Map<Long, LinkedHashSet<Genre>> buildGenresMap(List<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        String filmsInSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        return jdbcTemplate.query(
                String.format(
                        "SELECT film.id, genre.* " +
                                "FROM film " +
                                "LEFT JOIN film_genre ON film.id = film_genre.film_id " +
                                "LEFT JOIN genre ON genre.id = film_genre.genre_id " +
                                "WHERE film.id IN (%s)", filmsInSql),
                rs -> {
                    Map<Long, LinkedHashSet<Genre>> resultMap = new LinkedHashMap<>();
                    int rowNum = 0;

                    while (rs.next()) {
                        Long filmId = rs.getLong("film.id");

                        LinkedHashSet<Genre> genres = resultMap.getOrDefault(filmId, new LinkedHashSet<>());
                        Genre genre = new GenreDbStorage.GenreMapper().mapRow(rs, rowNum);
                        if (genre != null && genre.getId() != null && genre.getId() != 0L) {
                            genres.add(genre);
                            resultMap.put(filmId, genres);
                        }

                        rowNum++;
                    }

                    return resultMap;
                },
                filmIds.toArray()
        );
    }

    private Map<Long, LinkedHashSet<Director>> buildDirectorsMap(List<Film> films) {
        List<Long> filmIds = films.stream().map(Film::getId).collect(Collectors.toList());
        String filmsInSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));

        return jdbcTemplate.query(
                String.format(
                        "SELECT film.id, director.* " +
                                "FROM film " +
                                "LEFT JOIN film_director ON film_director.film_id = film.id " +
                                "LEFT JOIN director ON film_director.director_id = director.id " +
                                "WHERE film.id IN (%s)", filmsInSql),
                rs -> {
                    Map<Long, LinkedHashSet<Director>> resultMap = new LinkedHashMap<>();
                    int rowNum = 0;

                    while (rs.next()) {
                        Long filmId = rs.getLong("film.id");

                        LinkedHashSet<Director> directors = resultMap.getOrDefault(filmId, new LinkedHashSet<>());
                        Director director = new DirectorDbStorage.DirectorMapper().mapRow(rs, rowNum);
                        if (director != null && director.getId() != null && director.getId() != 0L) {
                            directors.add(director);
                            resultMap.put(filmId, directors);
                        }

                        rowNum++;
                    }

                    return resultMap;
                },
                filmIds.toArray()
        );
    }

    @Override
    public List<Film> getRecommendedFilms(Long userId) {
        Long bestUserId = null;

        try {
            bestUserId = jdbcTemplate.queryForObject(
                    "SELECT fl2.user_id " +
                            "FROM film_like fl " +
                            "JOIN film_like fl2 ON fl.film_id = fl2.film_id AND fl2.user_id != fl.user_id " +
                            "WHERE fl.user_id = ? " +
                            "GROUP BY fl.user_id, fl2.user_id " +
                            "ORDER BY COUNT(*) DESC " +
                            "LIMIT 1",
                    Long.class,
                    userId
            );
        } catch (EmptyResultDataAccessException ignore) {

        }

        if ( bestUserId == null ) {
            return Collections.emptyList();
        }

        List<Long> recommendedFilmIds = jdbcTemplate.query(
                "SELECT f.id " +
                        "FROM film as f " +
                        "LEFT JOIN film_like fl on f.id = fl.film_id and fl.user_id = ? " +
                        "LEFT JOIN film_like fl2 on f.id = fl2.film_id and fl2.user_id = ? " +
                        "WHERE fl.film_id IS NULL AND fl2.film_id IS NOT NULL;",
                (ResultSet rs, int rowNum) -> rs.getLong("film.id"),
                userId,
                bestUserId
        );

        return findFilmsAllById(recommendedFilmIds);
    }
}
