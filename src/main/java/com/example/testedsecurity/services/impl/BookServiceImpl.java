package com.example.testedsecurity.services.impl;

import com.example.testedsecurity.dtos.BookDto;
import com.example.testedsecurity.exceptions.BookNotFoundException;
import com.example.testedsecurity.repositories.BookRepository;
import com.example.testedsecurity.security.entities.Book;
import com.example.testedsecurity.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookDto createBook(BookDto bookDto) {
        bookRepository.save(new Book(null, bookDto.title()));
        return new BookDto(bookDto.title());
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public BookDto updateBook(Long id, String title) {
        Optional<Book> foundBook = bookRepository.findById(id);
        Book book = foundBook.orElseThrow(() -> new BookNotFoundException(String.format("Book with id %d not found.", id)));
        book.setTitle(title);
        bookRepository.save(book);
        return new BookDto(title);
    }

    @Override
    public Collection<BookDto> getAllBooks() {
        return null;
    }

    @Override
    public BookDto getBookById(Long id) {
        return null;
    }
}
