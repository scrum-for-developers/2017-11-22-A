package de.codecentric.psd.worblehat.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class StandardBookServiceTest {

	private BorrowingRepository borrowingRepository;

	private BookRepository bookRepository;

	private BookService bookService;

	private static final String BORROWER_EMAIL = "someone@codecentric.de";

	private static final DateTime NOW = DateTime.now();

	private static final Book TEST_BOOK = new Book("title", "author", "edition", "isbn", "lorem ipsum", 2016);

	@Before
	public void setup() throws Exception {
		borrowingRepository = mock(BorrowingRepository.class);
		bookRepository = mock(BookRepository.class);
		bookService = new StandardBookService(borrowingRepository, bookRepository);
	}

	@Test
	public void shouldReturnAllBooksOfOnePerson() {
		Borrowing borrowing = new Borrowing(TEST_BOOK, BORROWER_EMAIL, NOW);
		List<Borrowing> result = Collections.singletonList(borrowing);
		when(borrowingRepository.findBorrowingsByBorrower(BORROWER_EMAIL))
		.thenReturn(result);
		bookService.returnAllBooksByBorrower(BORROWER_EMAIL);
		verify(borrowingRepository).delete(borrowing);
	}

	@Test
	public void shouldSaveBorrowingWithBorrowerEmail() throws Exception {
		when(borrowingRepository.findBorrowingForBook(TEST_BOOK)).thenReturn(null);
		ArgumentCaptor<Borrowing> borrowingArgumentCaptor = ArgumentCaptor.forClass(Borrowing.class);
		bookService.borrowBook(TEST_BOOK, BORROWER_EMAIL);
		verify(borrowingRepository).save(borrowingArgumentCaptor.capture());
		assertThat(borrowingArgumentCaptor.getValue().getBorrowerEmailAddress(), is(BORROWER_EMAIL));
	}

	@Test(expected = BookAlreadyBorrowedException.class)
	public void shouldThrowExceptionWhenBookAlreadyBorrowed() throws Exception {
		when(borrowingRepository.findBorrowingForBook(TEST_BOOK)).thenReturn(new Borrowing(TEST_BOOK, BORROWER_EMAIL, NOW));
		bookService.borrowBook(TEST_BOOK, BORROWER_EMAIL);
	}

	@Test
	public void shouldCreateBook() throws Exception {
		ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
		bookService.createBook(TEST_BOOK.getTitle(), TEST_BOOK.getAuthor(), TEST_BOOK.getEdition(),
				TEST_BOOK.getIsbn(), TEST_BOOK.getDescription(), TEST_BOOK.getYearOfPublication());
		verify(bookRepository).save(bookArgumentCaptor.capture());
		assertThat(bookArgumentCaptor.getValue().getTitle(), is(TEST_BOOK.getTitle()));
		assertThat(bookArgumentCaptor.getValue().getAuthor(), is(TEST_BOOK.getAuthor()));
		assertThat(bookArgumentCaptor.getValue().getEdition(), is(TEST_BOOK.getEdition()));
		assertThat(bookArgumentCaptor.getValue().getIsbn(), is(TEST_BOOK.getIsbn()));
		assertThat(bookArgumentCaptor.getValue().getYearOfPublication(), is(TEST_BOOK.getYearOfPublication()));
	}

	@Test
	public void shouldFindAllBooks() throws Exception {
		List<Book> expectedBooks = new ArrayList<>();
		expectedBooks.add(TEST_BOOK);
		when(bookRepository.findAllBooks()).thenReturn(expectedBooks);
		List<Book> actualBooks = bookService.findAllBooks();
		assertThat(actualBooks, is(expectedBooks));
	}

	@Test
	public void shouldFindexistingBooks() throws Exception {
		when(bookRepository.findBookByIsbn(TEST_BOOK.getIsbn())).thenReturn(TEST_BOOK);
		Book actualBook = bookRepository.findBookByIsbn(TEST_BOOK.getIsbn());
		assertThat(actualBook, is(TEST_BOOK));
	}

	@Test
	public void shouldVerifyExistingBooks() throws Exception {
		when(bookRepository.findBookByIsbn(TEST_BOOK.getIsbn())).thenReturn(TEST_BOOK);
		Boolean bookExists = bookService.bookExists(TEST_BOOK.getIsbn());
		assertTrue(bookExists);
	}

	@Test
	public void shouldDeleteAllBooksAndBorrowings() throws Exception {
		bookService.deleteAllBooks();
		verify(bookRepository).deleteAll();
		verify(borrowingRepository).deleteAll();
	}

	@Test
	public void shouldCreateTwoBooks() throws Exception {
		ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
		bookService.createBook(TEST_BOOK.getTitle(), TEST_BOOK.getAuthor(), TEST_BOOK.getEdition(),
				TEST_BOOK.getIsbn(), TEST_BOOK.getDescription(), TEST_BOOK.getYearOfPublication());

		bookService.createBook(TEST_BOOK.getTitle(), TEST_BOOK.getAuthor(), TEST_BOOK.getEdition(),
				TEST_BOOK.getIsbn(), TEST_BOOK.getDescription(), TEST_BOOK.getYearOfPublication());
		verify(bookRepository,times(2)).save(bookArgumentCaptor.capture());
	}

	@Test
	public void shouldRejectBooksWithDifferentTitlesWithSameISBN() throws Exception {
		ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
		when(bookRepository.findBooksByIsbn(TEST_BOOK.getIsbn())).thenReturn(Arrays.asList(TEST_BOOK));

		bookService.createBook(TEST_BOOK.getTitle()+"X", TEST_BOOK.getAuthor(), TEST_BOOK.getEdition(),
				TEST_BOOK.getIsbn(), TEST_BOOK.getDescription(), TEST_BOOK.getYearOfPublication());
		verify(bookRepository,times(0)).save(bookArgumentCaptor.capture());
	}

	@Test
	public void shouldRejectBooksWithDifferentAuthorsWithSameISBN() throws Exception {
		ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
		when(bookRepository.findBooksByIsbn(TEST_BOOK.getIsbn())).thenReturn(Arrays.asList(TEST_BOOK));

		bookService.createBook(TEST_BOOK.getTitle(), TEST_BOOK.getAuthor()+"X", TEST_BOOK.getEdition(),
				TEST_BOOK.getIsbn(), TEST_BOOK.getDescription(), TEST_BOOK.getYearOfPublication());
		verify(bookRepository,times(0)).save(bookArgumentCaptor.capture());
	}

	@Test
	public void shouldRejectBooksWithDifferentAuthorsWithSameISBNAndBorrowing() throws Exception {
		ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
		Borrowing borrowing = new Borrowing(TEST_BOOK, BORROWER_EMAIL, NOW);
		TEST_BOOK.setBorrowing(borrowing);
		when(bookRepository.findBooksByIsbn(TEST_BOOK.getIsbn())).thenReturn(Arrays.asList(TEST_BOOK));

		bookService.createBook(TEST_BOOK.getTitle(), TEST_BOOK.getAuthor()+"X", TEST_BOOK.getEdition(),
				TEST_BOOK.getIsbn(), TEST_BOOK.getDescription(), TEST_BOOK.getYearOfPublication());
		verify(bookRepository,times(0)).save(bookArgumentCaptor.capture());
	}

	@Test
	public void shouldNotCreateBookWithEmptyISBN() throws Exception {
		ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
		bookService.createBook(TEST_BOOK.getTitle(), TEST_BOOK.getAuthor(), TEST_BOOK.getEdition(),
				TEST_BOOK.getIsbn(), TEST_BOOK.getDescription(), TEST_BOOK.getYearOfPublication());

		bookService.createBook(TEST_BOOK.getTitle(), TEST_BOOK.getAuthor(), TEST_BOOK.getEdition(),
				TEST_BOOK.getIsbn(), TEST_BOOK.getDescription(), TEST_BOOK.getYearOfPublication());
		verify(bookRepository,times(2)).save(bookArgumentCaptor.capture());
	}
}