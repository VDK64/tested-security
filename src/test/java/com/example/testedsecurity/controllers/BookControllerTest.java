package com.example.testedsecurity.controllers;

import com.example.testedsecurity.dtos.BookDto;
import com.example.testedsecurity.repositories.BookRepository;
import com.example.testedsecurity.security.entities.Book;
import com.example.testedsecurity.utilities.TokenUtility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.example.testedsecurity.Utility.asJsonString;
import static com.example.testedsecurity.properties.BookProperties.BOOK_REQUEST_MAPPING;
import static com.example.testedsecurity.security.entities.Role.ADMIN;
import static com.example.testedsecurity.security.entities.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/test-data.sql")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtility tokenUtility;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void successAdminGetAllBooksTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), "admin");
        mockMvc.perform(get(BOOK_REQUEST_MAPPING).header("Authorization", "Bearer " + token))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(List.of(new BookDto("first book")))));
    }

    @Test
    public void successUserGetAllBooksTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(USER), "user");
        mockMvc.perform(get(BOOK_REQUEST_MAPPING).header("Authorization", "Bearer " + token))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(List.of(new BookDto("first book")))));
    }

    @Test
    public void getAllBooksWithoutTokenTest() throws Exception {
        mockMvc.perform(get(BOOK_REQUEST_MAPPING))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void successGetBookByIdUserTest() throws Exception {
        Long bookId = bookRepository.findByTitle("first book")
                .orElseThrow()
                .getId();

        String token
                = tokenUtility.generateToken(List.of(USER), "user");

        mockMvc.perform(get("/book/" + bookId).header("Authorization", "Bearer " + token))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(new BookDto("first book"))));
    }

    @Test
    public void successGetBookByIdAdminTest() throws Exception {
        Long bookId = bookRepository.findByTitle("first book")
                .orElseThrow()
                .getId();

        String token
                = tokenUtility.generateToken(List.of(ADMIN), "admin");

        mockMvc.perform(get("/book/" + bookId).header("Authorization", "Bearer " + token))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(new BookDto("first book"))));
    }

    @Test
    public void getBookByIdWithoutTokenTest() throws Exception {
        Long bookId = bookRepository.findByTitle("first book")
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/book/" + bookId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createBookByAdminTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), "admin");

        mockMvc.perform(post(BOOK_REQUEST_MAPPING)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new BookDto("test book"))))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(new BookDto("test book"))));

        Book testBook = bookRepository.findByTitle("test book").orElseThrow();
        bookRepository.deleteById(testBook.getId());
    }

    @Test
    public void createBookByUserTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(USER), "user");

        mockMvc.perform(post(BOOK_REQUEST_MAPPING)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new BookDto("test book"))))
                .andExpect(status().isForbidden());

        Optional<Book> book = bookRepository.findByTitle("test book");
        assertEquals(Optional.empty(), book);
    }

    @Test
    public void createBookWithoutTokenTest() throws Exception {
        mockMvc.perform(post(BOOK_REQUEST_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new BookDto("test book"))))
                .andExpect(status().isUnauthorized());

        Optional<Book> book = bookRepository.findByTitle("test book");
        assertEquals(Optional.empty(), book);
    }

    @Test
    public void deleteBookByIdByAdminTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), "admin");

        Book savedBook = bookRepository.save(new Book(null, "test book"));

        mockMvc.perform(delete("/book/" + savedBook.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Optional<Book> book = bookRepository.findByTitle("test book");
        assertEquals(Optional.empty(), book);
    }


    @Test
    public void deleteBookByIdByUserTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(USER), "user");

        Book savedBook = bookRepository.save(new Book(null, "test book"));

        mockMvc.perform(delete("/book/" + savedBook.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void deleteBookByIdWithoutTokenTest() throws Exception {
        Book savedBook = bookRepository.save(new Book(null, "test book"));

        mockMvc.perform(delete("/book/" + savedBook.getId()))
                .andExpect(status().isUnauthorized());

        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void updateBookByAdminTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), "admin");

        Book savedBook = bookRepository.save(new Book(null, "test book"));

        mockMvc.perform(put("/book")
                        .header("Authorization", "Bearer " + token)
                        .param("id", String.valueOf(savedBook.getId()))
                        .param("title", "updated title"))
                .andExpect(status().isOk());

        Book book = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertEquals("updated title", book.getTitle());
        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void updateBookByUserTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(USER), "user");

        Book savedBook = bookRepository.save(new Book(null, "test book"));

        mockMvc.perform(put("/book")
                        .header("Authorization", "Bearer " + token)
                        .param("id", String.valueOf(savedBook.getId()))
                        .param("title", "updated title"))
                .andExpect(status().isForbidden());

        Book book = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertEquals("test book", book.getTitle());
        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void updateBookWithoutTokenTest() throws Exception {
        Book savedBook = bookRepository.save(new Book(null, "test book"));

        mockMvc.perform(put("/book")
                        .param("id", String.valueOf(savedBook.getId()))
                        .param("title", "updated title"))
                .andExpect(status().isUnauthorized());

        Book book = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertEquals("test book", book.getTitle());
        bookRepository.deleteById(savedBook.getId());
    }

}