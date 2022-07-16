CREATE TABLE IF NOT EXISTS RATINGS (
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(5)
);

ALTER TABLE RATINGS ALTER COLUMN id RESTART;

CREATE TABLE IF NOT EXISTS FILMS (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    name varchar(50),
    description varchar(200),
    release_date date,
    duration int,
    rating_id int REFERENCES RATINGS (id)
);

CREATE TABLE IF NOT EXISTS USERS (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    login varchar(30),
    email varchar(30),
    name varchar(50),
    birthday date
    );

CREATE TABLE IF NOT EXISTS LIKES (
    id int PRIMARY KEY AUTO_INCREMENT,
    film_id bigint REFERENCES FILMS (id) ON DELETE CASCADE,
    user_id bigint REFERENCES USERS (id) ON DELETE CASCADE
);

ALTER TABLE LIKES ADD UNIQUE (film_id, user_id);

CREATE TABLE IF NOT EXISTS GENRES (
    id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(30)
);

ALTER TABLE GENRES ALTER COLUMN id RESTART;

CREATE TABLE IF NOT EXISTS FILM_GENRES (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    film_id bigint REFERENCES FILMS (id) ON DELETE CASCADE,
    genre_id int REFERENCES GENRES (id)
);

CREATE TABLE IF NOT EXISTS FRIENDS (
    id bigint PRIMARY KEY AUTO_INCREMENT,
    requesting_user_id bigint REFERENCES USERS (id) ON DELETE CASCADE,
    requested_user_id bigint REFERENCES USERS (id) ON DELETE CASCADE,
    status boolean
);