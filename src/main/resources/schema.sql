/*DROP TABLE Films;
DROP TABLE Rating;
DROP TABLE Likes;
DROP TABLE Users;
DROP TABLE Genres;
DROP TABLE Genres_Relation;
DROP TABLE Friendship;
*/
CREATE TABLE IF NOT EXISTS Films(film_id int auto_increment,
				   name varchar(160),
				   description varchar(255),
				   releaseDate date,
				   duration int,
				   rating_id int
				  );

CREATE TABLE IF NOT EXISTS Rating(rating_id int auto_increment,
				   name varchar(160)
				  );

CREATE TABLE IF NOT EXISTS Likes(like_id int auto_increment,
				   film_id int,
				   user_id int
				  );
				 
CREATE TABLE IF NOT EXISTS Users(user_id int auto_increment,
				   login varchar(160),
				   name varchar(160),
				   email varchar(160),
				   birthday date
				  );

CREATE TABLE IF NOT EXISTS Genres(genre_id int auto_increment,
				   name varchar(160)
				  );

CREATE TABLE IF NOT EXISTS Genres_Relation(id int auto_increment,
                   film_id int,
                   genre_id int
				  );


CREATE TABLE IF NOT EXISTS Friendship(friendship_id int auto_increment,
                   user_id int,
                   friend_id int
				  );
				 
INSERT INTO Rating(rating_id, name)
values(1, 'G');
INSERT INTO Rating(rating_id, name)
values(2, 'PG');
INSERT INTO Rating(rating_id, name)
values(3, 'PG-13');
INSERT INTO Rating(rating_id, name)
values(4, 'R');
INSERT INTO Rating(rating_id, name)
values(5, 'NC-17');

INSERT INTO Genres(genre_id, name)
values(1, 'Комедия');
INSERT INTO Genres(genre_id, name)
values(2, 'Драма');
INSERT INTO Genres(genre_id, name)
values(3, 'Мультфильм');
INSERT INTO Genres(genre_id, name)
values(4, 'Триллер');
INSERT INTO Genres(genre_id, name)
values(5, 'Документальный');
INSERT INTO Genres(genre_id, name)
values(6, 'Боевик');

