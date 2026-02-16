package com.catalog.service;

import com.catalog.dto.BookResponseDTO;
import java.util.List;

public interface OpenLibraryService {

    void enrich(BookResponseDTO book);

    List<BookResponseDTO> searchByTitle(String title);

    List<BookResponseDTO> searchByAuthor(String author);
}
