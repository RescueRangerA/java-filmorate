INSERT INTO film_mpa_rating (title, description) VALUES ('G', 'У фильма нет возрастных ограничений') ON CONFLICT DO NOTHING;
INSERT INTO film_mpa_rating (title, description) VALUES ('PG', 'Детям рекомендуется смотреть фильм с родителями') ON CONFLICT DO NOTHING;
INSERT INTO film_mpa_rating (title, description) VALUES ('PG-13', 'Детям до 13 лет просмотр не желателен') ON CONFLICT DO NOTHING;
INSERT INTO film_mpa_rating (title, description) VALUES ('R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого') ON CONFLICT DO NOTHING;
INSERT INTO film_mpa_rating (title, description) VALUES ('NC-17', 'Лицам до 18 лет просмотр запрещён') ON CONFLICT DO NOTHING;


INSERT INTO genre (title) VALUES ('Комедия') ON CONFLICT DO NOTHING;
INSERT INTO genre (title) VALUES ('Драма') ON CONFLICT DO NOTHING;
INSERT INTO genre (title) VALUES ('Мультфильм') ON CONFLICT DO NOTHING;
INSERT INTO genre (title) VALUES ('Триллер') ON CONFLICT DO NOTHING;
INSERT INTO genre (title) VALUES ('Документальный') ON CONFLICT DO NOTHING;
INSERT INTO genre (title) VALUES ('Боевик') ON CONFLICT DO NOTHING;