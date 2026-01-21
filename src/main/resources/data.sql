DELETE FROM  "ratings";
INSERT INTO "ratings" ("id", "name")
VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');

DELETE FROM  "genres";
INSERT INTO "genres" ("id", "name")
VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

--DELETE FROM "directors";
--INSERT INTO "directors" ("name")
--VALUES ('Леонид Гайдай'), ('Андрей Тарковский'), ('Стивен Спилберг'), ('Кристофер Нолан');