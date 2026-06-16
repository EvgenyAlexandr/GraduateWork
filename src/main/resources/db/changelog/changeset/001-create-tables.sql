-- liquibase formatted sql

-- changeset homework:001-create-users
CREATE TABLE users
(
    id         SERIAL PRIMARY KEY,
    email      VARCHAR(32)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    first_name VARCHAR(16)  NOT NULL,
    last_name  VARCHAR(16)  NOT NULL,
    phone      VARCHAR(20),
    role       VARCHAR(10)  NOT NULL,
    image      VARCHAR(255)
);

-- changeset homework:002-create-ad
CREATE TABLE ad
(
    pk          SERIAL PRIMARY KEY,
    title       VARCHAR(32)  NOT NULL,
    description VARCHAR(64)  NOT NULL,
    price       INTEGER      NOT NULL CHECK (price >= 0 AND price <= 10000000),
    image       VARCHAR(255),
    author_id   INTEGER      NOT NULL REFERENCES users (id)
);

-- changeset homework:003-create-comment
CREATE TABLE comment
(
    pk         SERIAL PRIMARY KEY,
    text       VARCHAR(64) NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    author_id  INTEGER     NOT NULL REFERENCES users (id),
    ad_pk      INTEGER     NOT NULL REFERENCES ad (pk)
);

CREATE INDEX idx_ad_author_id ON ad (author_id);
CREATE INDEX idx_comment_ad_pk ON comment (ad_pk);
CREATE INDEX idx_comment_author_id ON comment (author_id);
