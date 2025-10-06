CREATE DATABASE moviedb;

USE moviedb;

CREATE TABLE movies (
    id VARCHAR(10) PRIMARY KEY,
    title VARCHAR(100) NOT NULL DEFAULT '',
    year INTEGER NOT NULL,
    director VARCHAR(100) NOT NULL DEFAULT ''
);

CREATE TABLE stars (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL DEFAULT '',
    birth_year INTEGER
);

CREATE TABLE stars_in_movies (
    star_id VARCHAR(10) NOT NULL,
    movie_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (star_id) REFERENCES stars(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    PRIMARY KEY (star_id, movie_id)
);

CREATE TABLE genres (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL DEFAULT ''
);

CREATE TABLE genres_in_movies (
    genre_id INTEGER NOT NULL,
    movie_id VARCHAR(10) NOT NULL,
    FOREIGN KEY (genre_id) REFERENCES genres(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    PRIMARY KEY (genre_id, movie_id)
);

CREATE TABLE credit_cards (
    id varchar(20) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL DEFAULT '',
    last_name VARCHAR(50) NOT NULL DEFAULT '',
    expiration DATE NOT NULL
);

CREATE TABLE customers (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL DEFAULT '',
    last_name VARCHAR(50) NOT NULL DEFAULT '',
    credit_card_id VARCHAR(20) NOT NULL,
    address VARCHAR(200) NOT NULL DEFAULT '',
    email VARCHAR(50) NOT NULL DEFAULT '',
    password VARCHAR(20) NOT NULL DEFAULT '',
    FOREIGN KEY (credit_card_id) REFERENCES credit_cards(id)
);

CREATE TABLE sales (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    customer_id INTEGER NOT NULL,
    movie_id VARCHAR(10) NOT NULL,
    sale_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
    );

CREATE TABLE ratings (
    movie_id VARCHAR(10) PRIMARY KEY,
    rating FLOAT NOT NULL,
    vote_count INTEGER NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);