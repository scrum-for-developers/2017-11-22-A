-- liquibase formatted sql

-- changeset action:drop_unique_index
DROP INDEX isbn ON book;

