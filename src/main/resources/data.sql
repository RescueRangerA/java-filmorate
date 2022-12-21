MERGE INTO film_mpa_rating VALUES (1, 'G', 'У фильма нет возрастных ограничений');
MERGE INTO film_mpa_rating VALUES (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями');
MERGE INTO film_mpa_rating VALUES (3, 'PG-13', 'Детям до 13 лет просмотр не желателен');
MERGE INTO film_mpa_rating VALUES (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
MERGE INTO film_mpa_rating VALUES (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

MERGE INTO genre VALUES (1, 'Комедия');
MERGE INTO genre VALUES (2, 'Драма');
MERGE INTO genre VALUES (3, 'Мультфильм');
MERGE INTO genre VALUES (4, 'Триллер');
MERGE INTO genre VALUES (5, 'Документальный');
MERGE INTO genre VALUES (6, 'Боевик');

-- SELECT film.*, film_mpa_rating.*, genre.*, COUNT(film_like.film_id) as likes FROM film
--                         LEFT JOIN film_like ON film.id = film_like.film_id
--                         LEFT JOIN film_mpa_rating ON film.rating_id = film_mpa_rating.id
--                         LEFT JOIN film_genre ON film.id = film_genre.film_id
--                         LEFT JOIN genre ON genre.id = film_genre.genre_id
--                         WHERE LOWER(FILM.TITLE) OR LOWER(film.description) LIKE 'ni'
--                         GROUP BY film.id
--                         ORDER BY likes DESC;