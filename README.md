# java-filmorate
Учебный проект Filmorate.

## Структура БД
![Filmorate ER-diargam](DB_ER.png)

**films** - таблица с данными по фильмам
* id - идентификатор фильма (первичный ключ)
* name - название фильма
* description - описание фильма
* release_dt - дата выпуска фильма
* duration - продолжительность фильма
* raiting_id - идентификатор  МРА-рейтинга фильма (внешний ключ к таблице raitings)

**ratings** - таблица с данными по рейтингам Ассоциации кинокомпаний
* id - идентификатор рейтинга (первичный ключ)
* name - название рейтинга ("PG", "PG-13" и т.п.)

**genres** - таблица с жанрами фильмов
* id - идентификатор жанра (первичный ключ)
* name - название жанра

**film_genres** - таблица с привязками жанров к фильмов (у одного фильма может быть несколько жанров)
* film_id - идентификатор фильма из таблицы films
* genre_id - идентификатор жанра из таблицы (genres)

**users** - таблица с данными по пользователям filmorate
* id - идентификатор пользователя (первичный ключ)
* email - электронная почта пользователя
* login - логин пользователя
* name - имя пользователя
* birthday - дата рождения пользователя

**film_likes** - таблица с данными по лайкам, поставленным пользователями фильмам
* film_id - идентификатор фильма
* user_id - идентификатор пользователя, поставившего лайк фильму

**friendship** - таблица с данными о том, кто с кем дружит в filmorate.
* user_id - идентификатор пользователя, добавившего друга
* friend_id - идентификатор пользователя, которого добавили в друзья

*В случае взаимной (подтверждённой) дружбы будет две записи: ("user1", "user2") и ("user2", "user1").
При неподтверждённой (односторонней) дружбе будет только запись ("user1", "user2").*

### Примеры запросов
1) Все данные о фильме:
```(sql)
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
```

2) Список идентификаторов топ-10 самых залайканных фильмов:
```(sql)
SELECT "film_id"
FROM "film_likes"
GROUP BY "film_id"
ORDER BY COUNT(1) DESC
LIMIT 10;
```

3) Список общих друзей для user_id1 и user_id2
```(sql)
SELECT *
FROM "users"
WHERE "id" IN (
    SELECT "friend_id" FROM "friendships" WHERE "user_id" = user_id1
    INTERSECT
    SELECT "friend_id" FROM "friendships" WHERE "user_id" = user_id2
);
```
