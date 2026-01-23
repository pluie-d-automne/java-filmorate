# java-filmorate
Учебный проект Filmorate.

## Команда
- Александр Крысанов (kukuruzco)
- Вадим Гришин (Vadim3092)
- Евгений	Б (evgvb)
- Максим Калачев (Kooooteeee)
- Полина Кудрявцева (pluie-d-automne)

## Распределение задач по команде
kanban-доска проекта: https://github.com/users/pluie-d-automne/projects/1

Александр Крысанов (kukuruzco):
- Функциональность «Рекомендации». 3 SP (add-recommendations)
- Вывод самых популярных фильмов по жанру и годам. 2 SP (add-most-populars)

Вадим Гришин (Vadim3092):
- Функциональность «Поиск». 3SP (add-search)
- Удаление фильмов и пользователей. 2 SP (add-remove-endpoint)

Евгений	Б (evgvb):
- Функциональность «Лента событий». 3 SP (add-feed)

Максим Калачев (Kooooteeee):
- Функциональность «Общие фильмы». 1 SP (add-common-films)
- Функциональность «Отзывы». 4 SP (add-reviews)

Полина Кудрявцева (pluie-d-automne):
- Добавление режиссёров в фильмы. 4 SP (add-director)

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

**directors** - таблица с режиссёрами
* id - идентификатор режиссёра (первичный ключ)
* name - имя режиссёра

**film_genres** - таблица с привязками жанров к фильмов (у одного фильма может быть несколько жанров)
* film_id - идентификатор фильма из таблицы films
* genre_id - идентификатор жанра из таблицы (genres)

**film_directors** - таблица с привязками режиссёров к фильмам (у одного фильма может быть несколько режиссёров)
* film_id - идентификатор фильма из таблицы films
* director_id - идентификатор режиссёра из таблицы (directors)

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

**reviews** - таблица с отзывами.
* review_id - идентификатор отзыва (первичный ключ)
* content - текст отзыва
* is_positive - флаг, положительный отзыв или отрицательный
* user_id - идентификатор пользователя, написавшего отзыв
* film_id - идентификатор фильма, на который написан отзыв
* useful - рейтинг полезности отзыва

**review_reactions** - реакции пользователей на отзывы.
* review_id - идентификатор отзыва из таблицы reviews
* user_id - идентификатор пользователя, поставившего лайк/дизлайк отзыву
* is_like - флаг, был поставлен лайк или дизлайк

**user_feeds** - логи для ленты событий.
* event_id - идентификатор события (первичный ключ)
* timestamp - временная метка события
* user_id - пользователь, совершивший действие
* event_type - тип события (LIKE, REVIEW или FRIEND)
* operation - тип произведённого действия (REMOVE, ADD, UPDATE)
* entity_id - идентификатор сущности (друга, фильма, отзыва)

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
