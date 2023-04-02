DROP TABLE IF EXISTS Films;
DROP TABLE IF EXISTS Mpa;
DROP TABLE IF EXISTS Likes;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Genres;
DROP TABLE IF EXISTS Genres_Relation;
DROP TABLE IF EXISTS Friendship;
DROP TABLE IF EXISTS Directors;
DROP TABLE IF EXISTS Directors_Relation;
DROP TABLE IF EXISTS Reviews;
DROP TABLE IF EXISTS ReviewLikes;
DROP TABLE IF EXISTS ReviewDislikes;
DROP TABLE IF EXISTS Events;

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

CREATE TABLE IF NOT EXISTS directors(director_id int auto_increment,
               name varchar(160)
              );

CREATE TABLE IF NOT EXISTS directors_relation(id int auto_increment,
                 film_id int,
                 director_id int
              );


CREATE TABLE IF NOT EXISTS Reviews(review_id int auto_increment,
                   content varchar(255),
                   isPositive boolean,
                   user_id int,
                   film_id int,
                   useful int
				  );

CREATE TABLE IF NOT EXISTS ReviewLikes(like_id int auto_increment,
				   review_id int,
				   user_id int
				  );

CREATE TABLE IF NOT EXISTS ReviewDislikes(dislike_id int auto_increment,
				   review_id int,
				   user_id int
				  );

CREATE TABLE IF NOT EXISTS Events(event_id bigint auto_increment,
                 eventtimestamp bigint,
                 user_id int,
                 eventtype varchar(6),
                 operation varchar(6),
                 entity_id int
              );

