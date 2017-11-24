package de.codecentric.psd.worblehat.domain;

import java.util.List;
import java.util.Optional;

/**
 * The interface of the domain service for books.
 */
public interface BookService {

	void returnAllBooksByBorrower(String string);

	void borrowBook(Book book, String borrower) throws BookAlreadyBorrowedException;

	Optional<Book> findFirstBookByIsbn(String isbn);

	List<Book> findAllBooks();

	Book createBook(String title, String author, String edition, String isbn, String description, int yearOfPublication);

	boolean bookExists(String isbn);

	void deleteAllBooks();
}
