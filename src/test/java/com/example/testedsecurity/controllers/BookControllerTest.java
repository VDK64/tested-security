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

    private static final String AUTHENTICATION_NAME_ADMIN = "admin";

    private static final String AUTHENTICATION_NAME_USER = "user";

    private static final String BEARER_PREFIX = "Bearer ";

    private final static String BOOK_URL_WITH_POSTFIX = "/book/";

    private static final String BOOK_URL = "/book";

    private static final String ID = "id";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BOOK_TITLE_FIRST = "first book";

    private static final String BOOK_TITLE_TEST = "test book";
    private static final String TITLE_PARAMETER = "title";

    private static final String UPDATED_TITLE = "updated title";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtility tokenUtility;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void successAdminGetAllBooksTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), AUTHENTICATION_NAME_ADMIN);
        mockMvc.perform(get(BOOK_REQUEST_MAPPING).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(List.of(new BookDto(BOOK_TITLE_FIRST)))));
    }

    @Test
    public void successUserGetAllBooksTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(USER), AUTHENTICATION_NAME_USER);
        mockMvc.perform(get(BOOK_REQUEST_MAPPING).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(List.of(new BookDto(BOOK_TITLE_FIRST)))));
    }

    @Test
    public void getAllBooksWithoutTokenTest() throws Exception {
        mockMvc.perform(get(BOOK_REQUEST_MAPPING))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void successGetBookByIdUserTest() throws Exception {
        Long bookId = bookRepository.findByTitle(BOOK_TITLE_FIRST)
                .orElseThrow()
                .getId();

        String token
                = tokenUtility.generateToken(List.of(USER), AUTHENTICATION_NAME_USER);

        mockMvc.perform(get(BOOK_URL_WITH_POSTFIX + bookId).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(new BookDto(BOOK_TITLE_FIRST))));
    }

    @Test
    public void successGetBookByIdAdminTest() throws Exception {
        Long bookId = bookRepository.findByTitle(BOOK_TITLE_FIRST)
                .orElseThrow()
                .getId();

        String token
                = tokenUtility.generateToken(List.of(ADMIN), AUTHENTICATION_NAME_ADMIN);

        mockMvc.perform(get(BOOK_URL_WITH_POSTFIX + bookId).header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(new BookDto(BOOK_TITLE_FIRST))));
    }

    @Test
    public void getBookByIdWithoutTokenTest() throws Exception {
        Long bookId = bookRepository.findByTitle(BOOK_TITLE_FIRST)
                .orElseThrow()
                .getId();

        mockMvc.perform(get(BOOK_URL_WITH_POSTFIX + bookId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void createBookByAdminTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), AUTHENTICATION_NAME_ADMIN);

        mockMvc.perform(post(BOOK_REQUEST_MAPPING)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new BookDto(BOOK_TITLE_TEST))))
                .andExpectAll(status().isOk(),
                        content().json(asJsonString(new BookDto(BOOK_TITLE_TEST))));

        Book testBook = bookRepository.findByTitle(BOOK_TITLE_TEST).orElseThrow();
        bookRepository.deleteById(testBook.getId());
    }

    @Test
    public void createBookByUserTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(USER), AUTHENTICATION_NAME_USER);

        mockMvc.perform(post(BOOK_REQUEST_MAPPING)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new BookDto(BOOK_TITLE_TEST))))
                .andExpect(status().isForbidden());

        Optional<Book> book = bookRepository.findByTitle(BOOK_TITLE_TEST);
        assertEquals(Optional.empty(), book);
    }

    @Test
    public void createBookWithoutTokenTest() throws Exception {
        mockMvc.perform(post(BOOK_REQUEST_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(new BookDto(BOOK_TITLE_TEST))))
                .andExpect(status().isUnauthorized());

        Optional<Book> book = bookRepository.findByTitle(BOOK_TITLE_TEST);
        assertEquals(Optional.empty(), book);
    }

    @Test
    public void deleteBookByIdByAdminTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), AUTHENTICATION_NAME_ADMIN);

        Book savedBook = bookRepository.save(new Book(null, BOOK_TITLE_TEST));

        mockMvc.perform(delete(BOOK_URL_WITH_POSTFIX + savedBook.getId())
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isOk());

        Optional<Book> book = bookRepository.findByTitle(BOOK_TITLE_TEST);
        assertEquals(Optional.empty(), book);
    }


    @Test
    public void deleteBookByIdByUserTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(USER), AUTHENTICATION_NAME_USER);

        Book savedBook = bookRepository.save(new Book(null, BOOK_TITLE_TEST));

        mockMvc.perform(delete(BOOK_URL_WITH_POSTFIX + savedBook.getId())
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token))
                .andExpect(status().isForbidden());

        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void deleteBookByIdWithoutTokenTest() throws Exception {
        Book savedBook = bookRepository.save(new Book(null, BOOK_TITLE_TEST));

        mockMvc.perform(delete(BOOK_URL_WITH_POSTFIX + savedBook.getId()))
                .andExpect(status().isUnauthorized());

        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void updateBookByAdminTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(ADMIN), AUTHENTICATION_NAME_ADMIN);

        Book savedBook = bookRepository.save(new Book(null, BOOK_TITLE_TEST));

        mockMvc.perform(put(BOOK_URL)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                        .param(ID, String.valueOf(savedBook.getId()))
                        .param(TITLE_PARAMETER, UPDATED_TITLE))
                .andExpect(status().isOk());

        Book book = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertEquals(UPDATED_TITLE, book.getTitle());
        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void updateBookByUserTest() throws Exception {
        String token
                = tokenUtility.generateToken(List.of(USER), AUTHENTICATION_NAME_USER);

        Book savedBook = bookRepository.save(new Book(null, BOOK_TITLE_TEST));

        mockMvc.perform(put(BOOK_URL)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + token)
                        .param(ID, String.valueOf(savedBook.getId()))
                        .param(TITLE_PARAMETER, UPDATED_TITLE))
                .andExpect(status().isForbidden());

        Book book = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertEquals(BOOK_TITLE_TEST, book.getTitle());
        bookRepository.deleteById(savedBook.getId());
    }

    @Test
    public void updateBookWithoutTokenTest() throws Exception {
        Book savedBook = bookRepository.save(new Book(null, BOOK_TITLE_TEST));

        mockMvc.perform(put(BOOK_URL)
                        .param(ID, String.valueOf(savedBook.getId()))
                        .param(TITLE_PARAMETER, UPDATED_TITLE))
                .andExpect(status().isUnauthorized());

        Book book = bookRepository.findById(savedBook.getId()).orElseThrow();
        assertEquals(BOOK_TITLE_TEST, book.getTitle());
        bookRepository.deleteById(savedBook.getId());
    }

}