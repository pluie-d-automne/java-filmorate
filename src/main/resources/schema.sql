DROP TABLE IF EXISTS  "users" CASCADE;
CREATE TABLE IF NOT EXISTS "users" (
  "id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "email" varchar NOT NULL,
  "login" varchar NOT NULL,
  "name" varchar,
  "birthday" date
);

DROP TABLE IF EXISTS "films" CASCADE;
CREATE TABLE IF NOT EXISTS "films" (
  "id" bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar(200),
  "release_dt" date,
  "duration" int,
  "rating_id" smallint
);

CREATE TABLE IF NOT EXISTS "ratings" (
  "id" smallint PRIMARY KEY,
  "name" varchar(5)
);

CREATE TABLE IF NOT EXISTS "genres" (
  "id" int PRIMARY KEY,
  "name" varchar(20)
);

DROP TABLE IF EXISTS "film_genres" CASCADE;
CREATE TABLE IF NOT EXISTS "film_genres" (
  "film_id" bigint,
  "genre_id" int
);

DROP TABLE IF EXISTS  "film_likes" CASCADE;
CREATE TABLE IF NOT EXISTS "film_likes" (
  "film_id" bigint,
  "user_id" bigint
);

DROP TABLE IF EXISTS  "friendships" CASCADE;
CREATE TABLE IF NOT EXISTS "friendships" (
  "user_id" bigint,
  "friend_id" bigint
);

ALTER TABLE "film_genres" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id") ON DELETE CASCADE;

ALTER TABLE "film_genres" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("id");

ALTER TABLE "films" ADD FOREIGN KEY ("rating_id") REFERENCES "ratings" ("id");

ALTER TABLE "film_likes" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "film_likes" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("id") ON DELETE CASCADE;

ALTER TABLE "friendships" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id") ON DELETE CASCADE;

ALTER TABLE "friendships" ADD FOREIGN KEY ("friend_id") REFERENCES "users" ("id") ON DELETE CASCADE;

CREATE UNIQUE INDEX IF NOT EXISTS unique_friendship ON "friendships" ("user_id", "friend_id");

CREATE UNIQUE INDEX IF NOT EXISTS unique_email ON "users" ("email");

CREATE UNIQUE INDEX IF NOT EXISTS unique_login ON "users" ("login");

CREATE UNIQUE INDEX IF NOT EXISTS unique_film_name ON "films" ("name", "release_dt");

CREATE UNIQUE INDEX IF NOT EXISTS unique_rating ON "ratings" ("name");

CREATE UNIQUE INDEX IF NOT EXISTS unique_genre ON "genres" ("name");

CREATE UNIQUE INDEX IF NOT EXISTS unique_film_genre ON "film_genres" ("film_id", "genre_id");

DROP VIEW IF EXISTS "films_full";
CREATE VIEW IF NOT EXISTS "films_full" AS
SELECT "films"."id",
       "films"."name",
       "films"."description",
       "films"."release_dt",
       "films"."duration",
       "films"."rating_id",
       "r"."name" AS "mpa_name",
       "g"."genres",
       "likes"."likes_cnt"
FROM "films"
LEFT JOIN "ratings" AS "r" ON "r"."id" = "films"."rating_id"
LEFT JOIN (SELECT "film_id", ARRAY_AGG("genre_id" ORDER BY "genre_id") AS "genres"
	FROM "film_genres"
	GROUP BY "film_id"
) AS "g" ON "g"."film_id" = "films"."id"
LEFT JOIN (SELECT "film_id", COUNT("user_id") AS "likes_cnt"
	FROM "film_likes"
	GROUP BY "film_id" ) AS "likes" ON "likes"."film_id" = "films"."id";