package com.example.testedsecurity.services;

import com.example.testedsecurity.dtos.BookDto;

import java.util.Collection;

public interface BookService {

    BookDto createBook(BookDto bookDto);

    void deleteBookById(Long id);

    BookDto updateBook(Long id, String title);

    Collection<BookDto> getAllBooks();

    BookDto getBookById(Long id);

}
