CREATE SCHEMA IF NOT EXISTS url_shortener;

CREATE TABLE url_shortener.url (
                                   id UUID PRIMARY KEY,
                                   original_url VARCHAR(2048) NOT NULL,
                                   shorter_url VARCHAR(255),
                                   status VARCHAR(50) NOT NULL,
                                   message TEXT
);