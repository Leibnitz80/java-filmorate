DROP TABLE IF EXISTS Films;
DROP TABLE IF EXISTS Mpa;
DROP TABLE IF EXISTS Likes;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Genres;
DROP TABLE IF EXISTS Genres_Relation;
DROP TABLE IF EXISTS Friendship;

CREATE TABLE IF NOT EXISTS Films(film_id int auto_increment,
				   name varchar(160),
				   description varchar(255),
				   releaseDate date,
				   duration int,
				   mpa_id int
				  );

CREATE TABLE IF NOT EXISTS Mpa(mpa_id int auto_increment,
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
				 
