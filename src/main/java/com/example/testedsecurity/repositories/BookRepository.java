package com.example.testedsecurity.repositories;

import com.example.testedsecurity.security.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
