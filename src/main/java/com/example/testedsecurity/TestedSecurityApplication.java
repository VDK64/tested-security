package com.example.testedsecurity;

import com.example.testedsecurity.repositories.BookRepository;
import com.example.testedsecurity.repositories.UserRepository;
import com.example.testedsecurity.security.entities.Book;
import com.example.testedsecurity.security.entities.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static com.example.testedsecurity.security.entities.Role.ADMIN;
import static com.example.testedsecurity.security.entities.Role.USER;

@SpringBootApplication
public class TestedSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestedSecurityApplication.class, args);
    }

    @Bean
    public CommandLineRunner createUser(BCryptPasswordEncoder encoder, UserRepository userRepository) {
        return args -> {
            User admin = new User(null, List.of(ADMIN), encoder.encode("p"), "admin",
                    true, true, true, true);
            User user = new User(null, List.of(USER), encoder.encode("p"), "user",
                    true, true, true, true);
            userRepository.save(admin);
            userRepository.save(user);
        };
    }

    @Bean
    public CommandLineRunner createBook(BookRepository bookRepository) {
        return args -> {
            bookRepository.save(new Book(null, "First book"));
        };
    }

}
