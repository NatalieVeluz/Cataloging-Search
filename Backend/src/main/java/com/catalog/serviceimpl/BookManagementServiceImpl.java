package com.catalog.serviceimpl;

import com.catalog.dto.BookResponseDTO;
import com.catalog.dto.SearchLogDTO;
import com.catalog.entity.Book;
import com.catalog.entity.PinnedBook;
import com.catalog.entity.SearchLog;
import com.catalog.entity.User;
import com.catalog.enums.Role;
import com.catalog.repository.BookRepository;
import com.catalog.repository.PinnedBookRepository;
import com.catalog.repository.SearchLogRepository;
import com.catalog.repository.UserRepository;
import com.catalog.service.BookManagementService;
import com.catalog.service.GoogleBooksService;
import com.catalog.service.LocService;
import com.catalog.service.OpenLibraryService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookManagementServiceImpl implements BookManagementService {

    private final BookRepository bookRepository;
    private final PinnedBookRepository pinnedBookRepository;
    private final SearchLogRepository searchLogRepository;
    private final UserRepository userRepository;
    private final LocService locService;
    private final GoogleBooksService googleBooksService;
    private final OpenLibraryService openLibraryService;

    public BookManagementServiceImpl(
            BookRepository bookRepository,
            PinnedBookRepository pinnedBookRepository,
            SearchLogRepository searchLogRepository,
            UserRepository userRepository,
            LocService locService,
            GoogleBooksService googleBooksService,
            OpenLibraryService openLibraryService) {

        this.bookRepository = bookRepository;
        this.pinnedBookRepository = pinnedBookRepository;
        this.searchLogRepository = searchLogRepository;
        this.userRepository = userRepository;
        this.locService = locService;
        this.googleBooksService = googleBooksService;
        this.openLibraryService = openLibraryService;
    }

    // =====================================================
    // 🔐 ADMIN CHECK
    // =====================================================
    private void checkAdmin(String userEmail) {

        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User email is required");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied. Admin only.");
        }
    }

    // =====================================================
    // 🛠 MANUAL ENTRY
    // =====================================================
    @Override
    public BookResponseDTO createManualBook(BookResponseDTO dto, String userEmail) {

        checkAdmin(userEmail);

        if (dto.getIsbn() == null || dto.getIsbn().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN is required");
        }

        if (bookRepository.findByIsbn(dto.getIsbn()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already exists in catalog");
        }

        locService.enrich(dto);
        googleBooksService.enrich(dto);
        openLibraryService.enrich(dto);

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthors(dto.getAuthors());
        book.setLccn(dto.getLccn());
        book.setCutterNumber(dto.getCutterNumber());
        book.setPublisher(dto.getPublisher());
        book.setEdition(dto.getEdition());
        book.setPublicationYear(dto.getPublicationYear());
        book.setCoverImageUrl(dto.getCoverImageUrl());
        book.setSummary(dto.getSummary());
        book.setContentNotes(dto.getContentNotes());
        book.setMetadataSource("Manual Entry + API Enriched");

        bookRepository.save(book);

        return mapToDTO(book);
    }

    // =====================================================
    // ✏ UPDATE BOOK
    // =====================================================
    @Override
    public BookResponseDTO updateBook(String isbn, BookResponseDTO dto, String userEmail) {

        checkAdmin(userEmail);

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        book.setTitle(dto.getTitle());
        book.setAuthors(dto.getAuthors());
        book.setLccn(dto.getLccn());
        book.setCutterNumber(dto.getCutterNumber());
        book.setPublisher(dto.getPublisher());
        book.setEdition(dto.getEdition());
        book.setPublicationYear(dto.getPublicationYear());
        book.setSummary(dto.getSummary());
        book.setContentNotes(dto.getContentNotes());
        book.setCoverImageUrl(dto.getCoverImageUrl());

        bookRepository.save(book);

        return mapToDTO(book);
    }

    // =====================================================
    // 📌 PIN BOOK
    // =====================================================
    @Override
    public BookResponseDTO pinBook(String isbn, String userEmail) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (pinnedBookRepository
                .findByBook_IdAndPinnedBy(book.getId(), userEmail)
                .isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already pinned");
        }

        PinnedBook pinned = new PinnedBook();
        pinned.setBook(book);
        pinned.setPinnedBy(userEmail);

        pinnedBookRepository.save(pinned);

        return mapToDTO(book);
    }

    // =====================================================
    // ❌ UNPIN BOOK
    // =====================================================
    @Override
    public void unpinBook(String isbn, String userEmail) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        PinnedBook pinned = pinnedBookRepository
                .findByBook_IdAndPinnedBy(book.getId(), userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book is not pinned by this user"));

        pinnedBookRepository.delete(pinned);
    }

    // =====================================================
    // 📚 VIEW PINNED BOOKS
    // =====================================================
    @Override
    public List<BookResponseDTO> getAllPinnedBooks(String userEmail) {

        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User email is required");
        }

        return pinnedBookRepository
                .findByPinnedBy(userEmail)
                .stream()
                .map(p -> mapToDTO(p.getBook()))
                .collect(Collectors.toList());
    }

    // =====================================================
    // 🔎 SEARCH LOGS (FULL METADATA VERSION)
    // =====================================================
    @Override
    public List<SearchLogDTO> getAllBooks(String keyword, String searchBy) {

        List<SearchLog> logs;

        if (keyword == null || keyword.isBlank()) {
            logs = searchLogRepository.findAllByOrderBySearchedAtDesc();
        } else {

            if (searchBy == null) searchBy = "title";

            switch (searchBy.toLowerCase()) {
                case "author":
                    logs = searchLogRepository
                            .findByBook_AuthorsContainingIgnoreCaseOrderBySearchedAtDesc(keyword);
                    break;
                case "isbn":
                    logs = searchLogRepository
                            .findByBook_IsbnContainingOrderBySearchedAtDesc(keyword);
                    break;
                default:
                    logs = searchLogRepository
                            .findByBook_TitleContainingIgnoreCaseOrderBySearchedAtDesc(keyword);
            }
        }

        return logs.stream()
                .filter(log -> log.getBook() != null)
                .limit(10)
                .map(log -> {

                    Book book = log.getBook();

                    SearchLogDTO dto = new SearchLogDTO();
                    dto.setId(log.getId());
                    dto.setIsbn(book.getIsbn());
                    dto.setTitle(book.getTitle());
                    dto.setAuthors(book.getAuthors());
                    dto.setCoverImageUrl(book.getCoverImageUrl());
                    dto.setPublicationYear(book.getPublicationYear());

                    // 🔥 ADD FULL METADATA
                    dto.setLccn(book.getLccn());
                    dto.setCutterNumber(book.getCutterNumber());
                    dto.setEdition(book.getEdition());
                    dto.setPublisher(book.getPublisher());
                    dto.setSummary(book.getSummary());
                    dto.setContentNotes(book.getContentNotes());
                    dto.setMetadataSource(book.getMetadataSource());

                    dto.setSearchedAt(log.getSearchedAt());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // =====================================================
    // 🗑 DELETE SEARCH LOG
    // =====================================================
    @Override
    public void deleteSearchLog(Long id) {

        SearchLog log = searchLogRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Search log not found"
                        ));

        searchLogRepository.delete(log);
    }

    // =====================================================
    // 🗑 DELETE ALL SEARCH LOGS (EXCEPT PINNED)
    // =====================================================
    @Override
    public void deleteAllSearchLogs() {

        searchLogRepository.deleteAllExceptPinned();
    }

    // =====================================================
    // 📖 GET BOOK BY ISBN
    // =====================================================
    @Override
    public BookResponseDTO getBookByIsbn(String isbn) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        return mapToDTO(book);
    }

    // =====================================================
    // 🔁 BOOK → DTO
    // =====================================================
    private BookResponseDTO mapToDTO(Book book) {

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
}
