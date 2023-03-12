# java-filmorate
![Database Diagram: ](https://github.com/Leibnitz80/java-filmorate/blob/main/Diagram.JPG)

:shipit: Some schema queries :point_down:

:gear: getFilmById:
select name, description, releaseDate, duration
from films
where film_id = @film_id

:gear: getUserFriends:
select u.user_id, u.login, u.name, u.email, u.birthday 
                     from Friendship f
                           inner join Users u on u.user_id = f.friend_id 
                     where f.user_id = @userId;

:gear: getCommonFriends:
select u.user_id, u.login, u.name, u.email, u.birthday
                     from Friendship f1 
                        inner join Friendship f2 on f2.friend_id = f1.friend_id 
                           inner join Users u on u.user_id = f2.friend_id 
                     where f1.user_id = @id1 and f2.user_id = @id2 
                       "and f1.friend_id <> f2.user_id and f2.friend_id <> f1.user_id;
