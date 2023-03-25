package com.example.testedsecurity.security.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.example.testedsecurity.properties.BookProperties.BOOK;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = BOOK)
@Table(name = BOOK)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

}
