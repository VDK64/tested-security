package com.example.testedsecurity.controllers;

import com.example.testedsecurity.dtos.BookDto;
import com.example.testedsecurity.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.example.testedsecurity.properties.BookProperties.*;

@RestController
@RequestMapping(BOOK_REQUEST_MAPPING)
@RequiredArgsConstructor
public class BookController {


    private final BookService bookService;

    @GetMapping
    public ResponseEntity<Collection<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping(PATH_VARIABLE_ID)
    public ResponseEntity<BookDto> getBookById(@PathVariable(name = VARIABLE_ID) Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@RequestBody BookDto bookDto) {
        return ResponseEntity.ok(bookService.createBook(bookDto));
    }

    @DeleteMapping(PATH_VARIABLE_ID)
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<BookDto> updateBook(@RequestParam(name = VARIABLE_ID) Long id,
                                              @RequestParam(name = VARIABLE_TITLE) String title) {
        return ResponseEntity.ok(bookService.updateBook(id, title));
    }

}
