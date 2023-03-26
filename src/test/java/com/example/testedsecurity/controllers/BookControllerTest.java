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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        assertEquals(book, Optional.empty());
    }

    @Test
    public void createBookWithoutTokenTest() throws Exception {
        mockMvc.perform(post(BOOK_REQUEST_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new BookDto("test book"))))
                .andExpect(status().isUnauthorized());

        Optional<Book> book = bookRepository.findByTitle("test book");
        assertEquals(book, Optional.empty());
    }

    @Test
    public void deleteBookByIdByAdminTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), "admin");

        mockMvc.perform(post(BOOK_REQUEST_MAPPING)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new BookDto("test book"))))
                .andExpect(status().isForbidden());

        Optional<Book> book = bookRepository.findByTitle("test book");
        assertEquals(book, Optional.empty());
    }



}