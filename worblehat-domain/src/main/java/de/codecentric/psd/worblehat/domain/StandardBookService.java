package de.codecentric.psd.worblehat.domain;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * The domain service class for book operations.
 */
@Service
@Transactional
public class StandardBookService implements BookService {
    public StandardBookService(){

    }

    @Autowired
    public StandardBookService(BorrowingRepository borrowingRepository, BookRepository bookRepository) {
        this.borrowingRepository = borrowingRepository;
        this.bookRepository = bookRepository;
    }

    private BorrowingRepository borrowingRepository;

    private BookRepository bookRepository;

    @Override
    public void returnAllBooksByBorrower(String borrowerEmailAddress) {
        List<Borrowing> borrowingsByUser = borrowingRepository
                .findBorrowingsByBorrower(borrowerEmailAddress);
        for (Borrowing borrowing : borrowingsByUser) {
            borrowingRepository.delete(borrowing);
        }
    }

    @Override
    public void borrowBook(Book book, String borrowerEmail) throws BookAlreadyBorrowedException {
        Borrowing borrowing = borrowingRepository.findBorrowingForBook(book);
        if (borrowing != null) {
            throw new BookAlreadyBorrowedException("Book is already borrowed");
        } else {
            borrowing = new Borrowing(book, borrowerEmail, new DateTime());
            borrowingRepository.save(borrowing);
        }
    }

    @Override
    public Optional<Book> findFirstBookByIsbn(String isbn) {

        return bookRepository.findBooksByIsbn(isbn).stream().findFirst();
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAllBooks();
    }


    @Override
    public Book createBook(String title, String author, String edition, String isbn, String description, int yearOfPublication) {
        Book book = new Book(title, author, edition, isbn, description, yearOfPublication);
        Optional<Book> bookByIsbn = findFirstBookByIsbn(book.getIsbn());

        if(bookByIsbn.isPresent() && !book.isSameActualBook(bookByIsbn.get())){
            return null;
        }
        return bookRepository.save(book);
    }

    @Override
    public boolean bookExists(String isbn) {
        return findFirstBookByIsbn(isbn) != null;
    }

    @Override
    public void deleteAllBooks() {
        borrowingRepository.deleteAll();
        bookRepository.deleteAll();
    }
}
