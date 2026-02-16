package com.catalog.service;

import com.catalog.dto.BookResponseDTO;
import java.util.List;

public interface LocService {

    // Used for ISBN enrichment
    void enrich(BookResponseDTO book);

    // LOC Primary Title Search
    List<BookResponseDTO> searchByTitle(String title);

    // LOC Primary Author Search
    List<BookResponseDTO> searchByAuthor(String author);
}
