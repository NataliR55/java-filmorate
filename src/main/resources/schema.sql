drop table if exists Friends, Likes, Users, Mpa, Film_Genre, Genres, Films;
CREATE TABLE IF NOT EXISTS Mpa (
    mpa_id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Films (
    film_id     INTEGER GENERATED BY DEFAULT AS IDENTITY Primary Key NOT NULL,
    name        varchar(100) NOT NULL,
    description varchar(200),
    releaseDate date,
    duration    int,
    mpa_id      INTEGER REFERENCES Mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS Users (
    user_id  INTEGER GENERATED BY DEFAULT AS IDENTITY Primary Key,
    email    varchar(30) NOT NULL,
    login    varchar(20) NOT NULL,
    name     varchar(20) NOT NULL,
    birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS Likes (
    film_id INTEGER REFERENCES Films (film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    CONSTRAINT unique_film_id_and_user_id UNIQUE(film_id, user_id)
);

CREATE TABLE IF NOT EXISTS Genres (
    genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     varchar(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Film_Genre (
    film_id  INTEGER REFERENCES Films (film_id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES Genres (genre_id) ON DELETE CASCADE,
    CONSTRAINT unique_film_id_and_genre_id UNIQUE(film_id, genre_id)
);
CREATE TABLE IF NOT EXISTS Friends (
    user_id   INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    friend_id INTEGER REFERENCES Users (user_id) ON DELETE CASCADE,
    status    varchar(20) NOT NULL,
    CONSTRAINT unique_user_id_and_friend_id UNIQUE(user_id, friend_id)
);



