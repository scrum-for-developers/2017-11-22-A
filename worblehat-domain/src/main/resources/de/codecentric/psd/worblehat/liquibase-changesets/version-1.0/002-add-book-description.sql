-- liquibase formatted sql

-- changeset action:add_book_description_column
ALTER TABLE book ADD description VARCHAR(20000);


-- changeset action:insert_book_with_description
INSERT INTO book(title, author, edition, isbn, description, year_of_publication)
VALUES
  ("Harry Potter and the Deathly Hallows", "J.K. Rowling", "", "0747595836", "lorem ipsum", 2007);
