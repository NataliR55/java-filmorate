insert into Mpa (mpa_name) values ('G'), ('PG'), ('PG-13'), ('R'), ('NC-17');
insert into Genres (name) values ('Комедия'), ('Драма'), ('Мультфильм'), ('Триллер'), ('Документальный'), ('Боевик');
/*
insert into Films (name,description,releaseDate,duration,mpa_id) values('1.The Green Mile','1.В тюрьме для смертников появляется заключенный с божественным даром.','1999-01-01',189,4);
insert into Film_Genre (film_id,genre_id) values(1,2);

insert into Films (name,description,releaseDate,duration,mpa_id)
values('2.Schindler s List','2.Фильм рассказывает реальную историю загадочного Оскара Шиндлера, члена нацистской партии, преуспевающего фабриканта, спасшего во время Второй мировой войны почти 1200 евреев.','1993-01-01',195,4);
insert into Film_Genre (film_id,genre_id) values(2,2);
insert into Film_Genre (film_id,genre_id) values(2,4);

insert into Films (name,description,releaseDate,duration,mpa_id)
values ('3.Coco','3.12-летний Мигель живёт в мексиканской деревушке в семье сапожников и тайно мечтает стать музыкантом.','2017-01-01',105,2);
insert into Film_Genre (film_id,genre_id) values(3,1);
insert into Film_Genre (film_id,genre_id) values(3,3);

insert into Films (name,description,releaseDate,duration,mpa_id) values('4.Film ','4.description ','2015-02-15',110,1);
insert into Film_Genre (film_id,genre_id) values(4,1);
insert into Film_Genre (film_id,genre_id) values(4,6);
---------------------------------------------------------------------------
insert into Users (email,login,name,birthday) values('user1@yandex.ru','login1', 'name1' ,'2001-01-15');
insert into Users (email,login,name,birthday) values('user2@yandex.ru','login2', 'name2' ,'2002-02-15');
insert into Users (email,login,name,birthday) values('user3@yandex.ru','login3', 'name3' ,'2003-03-15');
insert into Users (email,login,name,birthday) values('user4@yandex.ru','login4', 'name4' ,'2004-04-15');

//insert into Friends (user_id,friend_id,status) values(1,2,'UNCONFIRMED');
insert into Friends (user_id,friend_id,status) values(2,1,'UNCONFIRMED');
UPDATE Friends SET status= 'CONFIRMED' WHERE  user_id=1 and  FRIEND_ID=2;
UPDATE Friends SET status= 'CONFIRMED' WHERE  user_id=2 and  FRIEND_ID=1;

insert into Friends (user_id,friend_id,status) values(3,2,'UNCONFIRMED');
insert into Friends (user_id,friend_id,status) values(1,3,'UNCONFIRMED');
insert into Friends (user_id,friend_id,status) values(4,3,'UNCONFIRMED');
--DELETE FROM Users WHERE user_id = 3;
--------------------------------------------------------------------------
insert into Likes (film_id,user_id) values(1,1);
insert into Likes (film_id,user_id) values(1,2);
insert into Likes (film_id,user_id) values(1,3);
insert into Likes (film_id,user_id) values(1,4);
insert into Likes (film_id,user_id) values(2,1);
--DELETE FROM Users WHERE user_id = 1;
--DELETE FROM Films WHERE film_id = 1;
 */