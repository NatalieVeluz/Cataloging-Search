package com.catalog.serviceimpl;

import com.catalog.dto.BookResponseDTO;
import com.catalog.entity.Book;
import com.catalog.entity.SearchLog;
import com.catalog.enums.SearchType;
import com.catalog.repository.BookRepository;
import com.catalog.repository.SearchLogRepository;
import com.catalog.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BookAggregationServiceImpl
 *
 * This service handles book searching and aggregation logic.
 *
 * Responsibilities:
 * - Perform DB-first search strategy
 * - Fetch data from multiple external APIs (LOC, Google Books, Open Library)
 * - Merge and deduplicate results
 * - Apply metadata enrichment
 * - Generate Cutter numbers
 * - Save new books to database
 * - Log user searches
 *
 * This class is transactional to ensure data consistency
 * when saving books and logging searches.
 */
@Service
@Transactional
public class BookAggregationServiceImpl implements BookAggregationService {

    private final LocService locService;
    private final GoogleBooksService googleBooksService;
    private final OpenLibraryService openLibraryService;
    private final CutterNumberService cutterService;
    private final SearchLogRepository logRepository;
    private final BookRepository bookRepository;

    /**
     * Constructor-based dependency injection.
     */
    public BookAggregationServiceImpl(
            LocService locService,
            GoogleBooksService googleBooksService,
            OpenLibraryService openLibraryService,
            CutterNumberService cutterService,
            SearchLogRepository logRepository,
            BookRepository bookRepository) {

        this.locService = locService;
        this.googleBooksService = googleBooksService;
        this.openLibraryService = openLibraryService;
        this.cutterService = cutterService;
        this.logRepository = logRepository;
        this.bookRepository = bookRepository;
    }

    // =====================================================
    // ISBN SEARCH
    // =====================================================

    /**
     * Searches a book by ISBN using a Database-First strategy.
     *
     * Process:
     * 1. Check if book already exists in database.
     * 2. If not found, fetch metadata from external APIs.
     * 3. Enrich metadata.
     * 4. Apply default values if needed.
     * 5. Generate Cutter number.
     * 6. Save to database.
     * 7. Log search activity.
     *
     * @param isbn ISBN value to search
     * @param userEmail Email of the user performing search
     * @return BookResponseDTO containing complete metadata
     */
    @Override
    public BookResponseDTO searchByIsbn(String isbn, String userEmail) {

        Book book;

        Optional<Book> existing = bookRepository.findByIsbn(isbn);

        if (existing.isPresent()) {

            book = existing.get();

        } else {

            BookResponseDTO dto = new BookResponseDTO();
            dto.setIsbn(isbn);

            smartEnrich(dto);
            applyDefaults(dto);

            dto.setCutterNumber(
                    cutterService.generate(
                            dto.getAuthors() != null
                                    ? dto.getAuthors()
                                    : dto.getTitle()
                    )
            );

            if (dto.getMetadataSource() == null)
                dto.setMetadataSource("Multiple Sources");

            book = saveToDatabase(dto);
        }

        logSearch(book, isbn, SearchType.ISBN, userEmail);

        return convertToDTO(book);
    }

    // =====================================================
    // TITLE SEARCH
    // =====================================================

    /**
     * Searches books by title.
     *
     * Strategy:
     * - Retrieve manual database entries first (priority).
     * - Fetch results from external APIs.
     * - Deduplicate by ISBN.
     * - Persist new API results.
     * - Log all search activity.
     *
     * @param title Title keyword
     * @param userEmail Email of requesting user
     * @return List of BookResponseDTO
     */
    @Override
    public List<BookResponseDTO> searchByTitle(String title, String userEmail) {

        List<BookResponseDTO> combinedResults = new ArrayList<>();

        // Fetch from external APIs
        combinedResults.addAll(locService.searchByTitle(title));
        combinedResults.addAll(googleBooksService.searchByTitle(title));
        combinedResults.addAll(openLibraryService.searchByTitle(title));

        List<BookResponseDTO> apiResults = deduplicate(combinedResults);

        // Fetch manual entries from database
        List<Book> dbResults =
                bookRepository.findByTitleContainingIgnoreCaseOrderByIdDesc(title);

        List<BookResponseDTO> finalResults = new ArrayList<>();

        // Add DB results first
        for (Book dbBook : dbResults) {
            logSearch(dbBook, title, SearchType.TITLE, userEmail);
            finalResults.add(convertToDTO(dbBook));
        }

        // Process API results
        for (BookResponseDTO dto : apiResults) {

            Book book;

            Optional<Book> existing =
                    bookRepository.findByIsbn(dto.getIsbn());

            if (existing.isPresent()) {

                book = existing.get();

            } else {

                smartEnrich(dto);
                applyDefaults(dto);

                dto.setCutterNumber(
                        cutterService.generate(
                                dto.getAuthors() != null
                                        ? dto.getAuthors()
                                        : dto.getTitle()
                        )
                );

                if (dto.getMetadataSource() == null)
                    dto.setMetadataSource("Multiple Sources");

                book = saveToDatabase(dto);
            }

            logSearch(book, title, SearchType.TITLE, userEmail);

            boolean alreadyAdded = finalResults.stream()
                    .anyMatch(b -> b.getIsbn().equals(book.getIsbn()));

            if (!alreadyAdded) {
                finalResults.add(convertToDTO(book));
            }
        }

        return finalResults;
    }

    // =====================================================
    // AUTHOR SEARCH
    // =====================================================

    /**
     * Searches books by author.
     *
     * @param author Author keyword
     * @param userEmail Email of requesting user
     * @return List of BookResponseDTO
     */
    @Override
    public List<BookResponseDTO> searchByAuthor(String author, String userEmail) {

        List<BookResponseDTO> combinedResults = new ArrayList<>();

        combinedResults.addAll(locService.searchByAuthor(author));
        combinedResults.addAll(googleBooksService.searchByAuthor(author));
        combinedResults.addAll(openLibraryService.searchByAuthor(author));

        List<BookResponseDTO> apiResults = deduplicate(combinedResults);

        List<Book> dbResults =
                bookRepository.findByAuthorsContainingIgnoreCaseOrderByIdDesc(author);

        List<BookResponseDTO> finalResults = new ArrayList<>();

        for (Book dbBook : dbResults) {
            logSearch(dbBook, author, SearchType.AUTHOR, userEmail);
            finalResults.add(convertToDTO(dbBook));
        }

        for (BookResponseDTO dto : apiResults) {

            Book book;

            Optional<Book> existing =
                    bookRepository.findByIsbn(dto.getIsbn());

            if (existing.isPresent()) {

                book = existing.get();

            } else {

                smartEnrich(dto);
                applyDefaults(dto);

                dto.setCutterNumber(
                        cutterService.generate(
                                dto.getAuthors() != null
                                        ? dto.getAuthors()
                                        : dto.getTitle()
                        )
                );

                if (dto.getMetadataSource() == null)
                    dto.setMetadataSource("Multiple Sources");

                book = saveToDatabase(dto);
            }

            logSearch(book, author, SearchType.AUTHOR, userEmail);

            boolean alreadyAdded = finalResults.stream()
                    .anyMatch(b -> b.getIsbn().equals(book.getIsbn()));

            if (!alreadyAdded) {
                finalResults.add(convertToDTO(book));
            }
        }

        return finalResults;
    }

    // =====================================================
    // SMART ENRICHMENT
    // =====================================================

    /**
     * Performs prioritized metadata enrichment.
     *
     * Priority Order:
     * 1. Library of Congress
     * 2. Google Books
     * 3. Open Library
     *
     * Missing fields are merged only if null.
     *
     * @param book BookResponseDTO to enrich
     */
    private void smartEnrich(BookResponseDTO book) {

        locService.enrich(book);

        BookResponseDTO googleTemp = new BookResponseDTO();
        googleTemp.setIsbn(book.getIsbn());
        googleBooksService.enrich(googleTemp);

        BookResponseDTO openTemp = new BookResponseDTO();
        openTemp.setIsbn(book.getIsbn());
        openLibraryService.enrich(openTemp);

        mergeIfNull(book, googleTemp);
        mergeIfNull(book, openTemp);
    }

    /**
     * Merges metadata only if the main record has null values.
     */
    private void mergeIfNull(BookResponseDTO main, BookResponseDTO fallback) {

        if (main.getTitle() == null) main.setTitle(fallback.getTitle());
        if (main.getAuthors() == null) main.setAuthors(fallback.getAuthors());
        if (main.getPublisher() == null) main.setPublisher(fallback.getPublisher());
        if (main.getPublicationYear() == null) main.setPublicationYear(fallback.getPublicationYear());
        if (main.getEdition() == null) main.setEdition(fallback.getEdition());
        if (main.getSummary() == null) main.setSummary(fallback.getSummary());
        if (main.getContentNotes() == null) main.setContentNotes(fallback.getContentNotes());
        if (main.getCoverImageUrl() == null) main.setCoverImageUrl(fallback.getCoverImageUrl());
    }

    /**
     * Applies default values when metadata is missing.
     */
    private void applyDefaults(BookResponseDTO book) {

        if (book.getTitle() == null)
            book.setTitle("Unknown Title");

        if (book.getAuthors() == null)
            book.setAuthors("Unknown Author");

        if (book.getLccn() == null)
            book.setLccn("Not Assigned by LOC");
    }

    /**
     * Deduplicates results using ISBN as unique key.
     *
     * @param list List of books
     * @return Deduplicated list
     */
    private List<BookResponseDTO> deduplicate(List<BookResponseDTO> list) {

        Map<String, BookResponseDTO> uniqueMap = list.stream()
                .filter(book -> book.getIsbn() != null)
                .collect(Collectors.toMap(
                        BookResponseDTO::getIsbn,
                        book -> book,
                        (existing, replacement) -> existing
                ));

        return new ArrayList<>(uniqueMap.values());
    }

    // =====================================================
    // DATABASE PERSISTENCE
    // =====================================================

    /**
     * Saves book to database.
     * Returns existing book if already present.
     *
     * @param dto BookResponseDTO
     * @return Persisted Book entity
     */
    private Book saveToDatabase(BookResponseDTO dto) {

        if (dto.getIsbn() == null)
            throw new RuntimeException("ISBN cannot be null when saving book.");

        Optional<Book> existing = bookRepository.findByIsbn(dto.getIsbn());
        if (existing.isPresent()) return existing.get();

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthors(dto.getAuthors());
        book.setLccn(dto.getLccn());
        book.setCutterNumber(dto.getCutterNumber());
        book.setCoverImageUrl(dto.getCoverImageUrl());
        book.setPublicationYear(dto.getPublicationYear());
        book.setEdition(dto.getEdition());
        book.setPublisher(dto.getPublisher());
        book.setSummary(dto.getSummary());
        book.setContentNotes(dto.getContentNotes());
        book.setMetadataSource(dto.getMetadataSource());

        return bookRepository.save(book);
    }

    /**
     * Converts Book entity to BookResponseDTO.
     */
    private BookResponseDTO convertToDTO(Book book) {

        BookResponseDTO dto = new BookResponseDTO();
        dto.setIsbn(book.getIsbn());
        dto.setTitle(book.getTitle());
        dto.setAuthors(book.getAuthors());
        dto.setLccn(book.getLccn());
        dto.setCutterNumber(book.getCutterNumber());
        dto.setCoverImageUrl(book.getCoverImageUrl());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setEdition(book.getEdition());
        dto.setPublisher(book.getPublisher());
        dto.setSummary(book.getSummary());
        dto.setContentNotes(book.getContentNotes());
        dto.setMetadataSource(book.getMetadataSource());

        return dto;
    }

    /**
     * Logs search activity into SearchLog table.
     *
     * @param book Book entity
     * @param query Search query value
     * @param type Type of search performed
     * @param userEmail Email of user performing search
     */
    private void logSearch(Book book, String query, SearchType type, String userEmail) {

        if (book == null)
            throw new RuntimeException("Cannot log search. Book is null.");

        SearchLog log = new SearchLog();
        log.setQueryValue(query);
        log.setSearchType(type);
        log.setUserEmail(userEmail);
        log.setBook(book);

        logRepository.save(log);
    }
}