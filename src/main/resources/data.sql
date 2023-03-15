DELETE FROM Rating;
DELETE FROM Genres;

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

