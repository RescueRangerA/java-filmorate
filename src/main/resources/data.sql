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
