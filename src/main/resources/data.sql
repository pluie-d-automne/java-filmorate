DELETE FROM  "ratings";
INSERT INTO "ratings" ("id", "name")
VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');

DELETE FROM  "genres";
INSERT INTO "genres" ("id", "name")
VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

--DELETE FROM  "users";
--INSERT INTO "users" ("email", "login", "name", "birthday")
--VALUES ('ivan@test.com', 'ivan', 'Иван', '1980-03-21'),
--       ('maria@test.com', 'maria', 'Марья', '1990-05-31');

DELETE FROM  "films";
INSERT INTO "films" ("name", "description", "release_dt", "duration", "rating_id")
VALUES ('Бриллиантовая рука', '-', '1969-04-28', 94, 3),
 ('Шрэк', '-', '2001-04-22', 90, 2);