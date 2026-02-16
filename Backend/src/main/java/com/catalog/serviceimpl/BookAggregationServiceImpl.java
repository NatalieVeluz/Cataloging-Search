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

@Service
@Transactional
public class BookAggregationServiceImpl implements BookAggregationService {

    private final LocService locService;
    private final GoogleBooksService googleBooksService;
    private final OpenLibraryService openLibraryService;
    private final CutterNumberService cutterService;
    private final SearchLogRepository logRepository;
    private final BookRepository bookRepository;

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
    @Override
    public List<BookResponseDTO> searchByTitle(String title, String userEmail) {

        List<BookResponseDTO> combinedResults = new ArrayList<>();

        combinedResults.addAll(locService.searchByTitle(title));
        combinedResults.addAll(googleBooksService.searchByTitle(title));
        combinedResults.addAll(openLibraryService.searchByTitle(title));

        List<BookResponseDTO> books = deduplicate(combinedResults);
        List<BookResponseDTO> finalResults = new ArrayList<>();

        for (BookResponseDTO dto : books) {

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

                book = saveToDatabase(dto);
            }

            logSearch(book, title, SearchType.TITLE, userEmail);
            finalResults.add(convertToDTO(book));
        }

        return finalResults;
    }

    // =====================================================
    // AUTHOR SEARCH
    // =====================================================
    @Override
    public List<BookResponseDTO> searchByAuthor(String author, String userEmail) {

        List<BookResponseDTO> combinedResults = new ArrayList<>();

        combinedResults.addAll(locService.searchByAuthor(author));
        combinedResults.addAll(googleBooksService.searchByAuthor(author));
        combinedResults.addAll(openLibraryService.searchByAuthor(author));

        List<BookResponseDTO> books = deduplicate(combinedResults);
        List<BookResponseDTO> finalResults = new ArrayList<>();

        for (BookResponseDTO dto : books) {

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

                book = saveToDatabase(dto);
            }

            logSearch(book, author, SearchType.AUTHOR, userEmail);
            finalResults.add(convertToDTO(book));
        }

        return finalResults;
    }

    // =====================================================
    // SMART ENRICHMENT
    // =====================================================
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

    private void applyDefaults(BookResponseDTO book) {

        if (book.getTitle() == null)
            book.setTitle("Unknown Title");

        if (book.getAuthors() == null)
            book.setAuthors("Unknown Author");

        if (book.getLccn() == null)
            book.setLccn("Not Assigned by LOC");
    }

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
    // SAVE BOOK & RETURN ENTITY
    // =====================================================
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

    // =====================================================
    // SAVE SEARCH LOG WITH FK
    // =====================================================
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
