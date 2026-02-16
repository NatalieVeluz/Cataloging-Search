package com.catalog.service;

import com.catalog.dto.BookResponseDTO;
import java.util.List;

public interface BookAggregationService {

    BookResponseDTO searchByIsbn(String isbn, String userEmail);

    List<BookResponseDTO> searchByTitle(String title, String userEmail);

    List<BookResponseDTO> searchByAuthor(String author, String userEmail);
}
