package com.example.testedsecurity.services.impl;

import com.example.testedsecurity.dtos.BookDto;
import com.example.testedsecurity.exceptions.BookAlreadyExistsException;
import com.example.testedsecurity.exceptions.BookNotFoundException;
import com.example.testedsecurity.repositories.BookRepository;
import com.example.testedsecurity.security.entities.Book;
import com.example.testedsecurity.services.BookService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.example.testedsecurity.properties.ExceptionsProperties.BOOK_ALREADY_EXISTS_MESSAGE;
import static com.example.testedsecurity.properties.ExceptionsProperties.BOOK_WITH_ID_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class BookServiceImplTest {

    @Autowired
    private BookService underTest;

    @MockBean
    private BookRepository bookRepository;

    @Test
    public void createBookSuccessfullyTest() {
        Book book = new Book(null, "first");
        BookDto bookDto = new BookDto("first");
        when(bookRepository.save(book)).thenReturn(new Book(1L, "first"));

        BookDto result = underTest.createBook(bookDto);
        Assertions.assertEquals(bookDto.title(), result.title());
    }

    @Test
    public void createAlreadyExistsBookTest() {
        Exception exception = assertThrows(BookAlreadyExistsException.class, () -> {
            Book book = new Book(null, "first");
            BookDto bookDto = new BookDto("first");
            when(bookRepository.save(book)).thenThrow(new BookAlreadyExistsException(BOOK_ALREADY_EXISTS_MESSAGE));

            underTest.createBook(bookDto);
        });
        assertTrue(exception.getMessage().contains(BOOK_ALREADY_EXISTS_MESSAGE));
    }

    @Test
    public void deleteBookTest() {
        underTest.deleteBookById(1L);
        verify(bookRepository, atMostOnce()).deleteById(1L);
    }

    @Test
    public void successfullyUpdateBook() {
        Book foundBook = new Book(1L, "test");
        Book updatedBook = new Book(1L, "updated");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(foundBook));
        verify(bookRepository, atMostOnce()).save(updatedBook);

        BookDto bookDto = underTest.updateBook(1L, "updated");
        assertEquals("updated", bookDto.title());
    }

    @Test
    public void updateBookExceptionTest() {
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());
            underTest.updateBook(1L, "updated");
        });
        assertTrue(exception.getMessage().contains(String.format(BOOK_WITH_ID_NOT_FOUND_MESSAGE, 1L)));
    }

    @Test
    public void getAllBooksTest() {
        BookDto bookDto = new BookDto("test");
        List<BookDto> testList = List.of(bookDto);
        List<Book> books = List.of(new Book(1L, "test"));
        when(bookRepository.findAll()).thenReturn(books);
        Collection<BookDto> allBooks = underTest.getAllBooks();
        assertEquals(1, allBooks.size());
        assertTrue(allBooks.contains(bookDto));
    }

    @Test
    public void successfullyGetBookByIdTest() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(new Book(1L, "test")));
        BookDto bookById = underTest.getBookById(1L);
        assertEquals("test", bookById.title());
    }

    @Test
    public void getBookByIdExceptionTest() {
        Exception exception = assertThrows(BookNotFoundException.class, () -> {
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());
            underTest.getBookById(1L);
        });
        assertTrue(exception.getMessage().contains(String.format(BOOK_WITH_ID_NOT_FOUND_MESSAGE, 1L)));
    }

}