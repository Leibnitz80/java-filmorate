![Database Diagram: ](https://github.com/Leibnitz80/java-filmorate/blob/main/Diagram.JPG)

:shipit: Добавлен учёт режисёров фильмов, отзывов и оценка отзывов пользователями, добавлен учёт Ленты событий.
Организован поиск по названию фильмов, режисёру. Создан отбор фильмов по рекомендациям, а также фильмов, общих между пользователями.
Создан функционал удаления фильмов и пользователей.  

:shipit: Some schema queries :point_down:

:gear: getFilmById:
select f.film_id, f.name, f.description, f.releaseDate, f.duration, r.mpa_id, r.name as mpa_name
from Films f
inner join Mpa r on r.mpa_id = f.mpa_id
where f.film_id = ?;

:gear: getAllFriends:
select u.user_id, u.login, u.name, u.email, u.birthday
from Friendship f
inner join Users u on u.user_id = f.friend_id
where f.user_id = ?
order by u.user_id;

:gear: getCommonFriends:
select u.user_id, u.login, u.name, u.email, u.birthday
from Friendship f1
inner join Friendship f2 on f2.friend_id = f1.friend_id
inner join Users u on u.user_id = f2.friend_id
where f1.user_id = ? and f2.user_id = ?
and f1.friend_id <> f2.user_id and f2.friend_id <> f1.user_id;
