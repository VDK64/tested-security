package com.example.testedsecurity.services.impl;

import com.example.testedsecurity.dtos.BookDto;
import com.example.testedsecurity.exceptions.BookAlreadyExistsException;
import com.example.testedsecurity.exceptions.BookNotFoundException;
import com.example.testedsecurity.repositories.BookRepository;
import com.example.testedsecurity.security.entities.Book;
import com.example.testedsecurity.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.testedsecurity.properties.ExceptionsProperties.BOOK_ALREADY_EXISTS_MESSAGE;
import static com.example.testedsecurity.properties.ExceptionsProperties.BOOK_WITH_ID_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookDto createBook(BookDto bookDto) {
        try{
            bookRepository.save(new Book(null, bookDto.title()));
        } catch (DataIntegrityViolationException exception) {
            throw new BookAlreadyExistsException(BOOK_ALREADY_EXISTS_MESSAGE);
        }
        return new BookDto(bookDto.title());
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateBook(Long id, String title) {
        Optional<Book> foundBook = bookRepository.findById(id);
        Book book = foundBook.orElseThrow(() -> new BookNotFoundException(String.format(BOOK_WITH_ID_NOT_FOUND_MESSAGE, id)));
        book.setTitle(title);
        bookRepository.save(book);
        return new BookDto(title);
    }

    @Override
    public Collection<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> new BookDto(book.getTitle()))
                .collect(Collectors.toList());
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new BookNotFoundException(String.format(BOOK_WITH_ID_NOT_FOUND_MESSAGE, id)));
        return new BookDto(book.getTitle());
    }

}
