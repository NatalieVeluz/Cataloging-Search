package com.catalog.repository;

import com.catalog.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    // 🔎 SEARCH BY TITLE (DB FIRST)
    List<Book> findByTitleContainingIgnoreCaseOrderByIdDesc(String title);

    // 🔎 SEARCH BY AUTHOR (DB FIRST)
    List<Book> findByAuthorsContainingIgnoreCaseOrderByIdDesc(String authors);

    // 🔎 SEARCH BY ISBN PARTIAL
    List<Book> findByIsbnContainingOrderByIdDesc(String isbn);
}
