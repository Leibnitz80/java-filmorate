# java-filmorate
![Database Diagram: ](https://github.com/Leibnitz80/java-filmorate/blob/main/Diagram.JPG)

:shipit: Some schema queries :point_down:

:gear: getFilmById:
select name, description, releaseDate, duration
from films
where film_id = @film_id

:gear: getUserFriends:
select case when second_user_id = @user_id then first_user_id
                                           else second_user_id
       end
from friendship
where @user_id in (first_user_id,second_user_id)
and approved = 1

:gear: getCommonFriends:
select second_user_id
from friendship f1
      inner join friendship f2 on f2.first_user_id in (@user_id2, f1.second_user_id) 
                              and f2.second_user_id in (@user_id2, f1.second_user_id)
                              and f2.approved = f1.approved
where f1.first_user_id = @user_id1
and f1.second_user_id <> @user_id2
and f1.approved = 1
union all
select f1.first_user_id
from friendship f1
      inner join friendship f2 on f2.first_user_id in (@user_id2, f1.second_user_id) 
                              and f2.second_user_id in (@user_id2, f1.second_user_id)
                              and f2.approved = f1.approved
where f1.second_user_id = @user_id1
and f1.first_user_id <> @user_id2
and f1.approved = 1
