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

/**
 * BookManagementServiceImpl
 *
 * Concrete implementation of BookManagementService.
 *
 * This service manages internal catalog operations including:
 * - Manual book creation
 * - Book metadata updates
 * - Pin and unpin functionality
 * - Retrieval of pinned books
 * - Search log viewing and filtering
 * - Administrative deletion of search logs
 *
 * Role-Based Access Control (RBAC) is enforced for
 * administrative operations.
 *
 * The class is annotated with @Transactional to ensure
 * atomic database operations and data consistency.
 */
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

    /**
     * Constructor-based dependency injection.
     *
     * @param bookRepository Repository for Book entity
     * @param pinnedBookRepository Repository for PinnedBook entity
     * @param searchLogRepository Repository for SearchLog entity
     * @param userRepository Repository for User entity
     * @param locService Library of Congress service
     * @param googleBooksService Google Books service
     * @param openLibraryService Open Library service
     */
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
    // ADMIN VALIDATION
    // =====================================================

    /**
     * Validates that the requesting user has ADMIN role.
     *
     * Validation Steps:
     * 1. Ensure email is not null or blank.
     * 2. Ensure user exists in the system.
     * 3. Ensure user role is ADMIN.
     *
     * @param userEmail Email of requesting user
     * @throws ResponseStatusException if validation fails
     */
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
    // MANUAL BOOK CREATION
    // =====================================================

    /**
     * Creates a new manual book entry in the catalog.
     *
     * Workflow:
     * - Validate ADMIN role
     * - Validate ISBN presence
     * - Ensure book does not already exist
     * - Enrich metadata using external services
     * - Save to database
     *
     * @param dto BookResponseDTO containing manual input
     * @param userEmail Email of requesting ADMIN
     * @return Saved book as BookResponseDTO
     */
    @Override
    public BookResponseDTO createManualBook(BookResponseDTO dto, String userEmail) {

        checkAdmin(userEmail);

        if (dto.getIsbn() == null || dto.getIsbn().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN is required");
        }

        if (bookRepository.findByIsbn(dto.getIsbn()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Book already exists in catalog");
        }

        // Enrich metadata from APIs
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
    // UPDATE BOOK
    // =====================================================

    /**
     * Updates an existing book’s metadata.
     *
     * @param isbn ISBN of book to update
     * @param dto Updated book information
     * @param userEmail Email of requesting ADMIN
     * @return Updated book as DTO
     */
    @Override
    public BookResponseDTO updateBook(String isbn, BookResponseDTO dto, String userEmail) {

        checkAdmin(userEmail);

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

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
    // PIN BOOK
    // =====================================================

    /**
     * Pins a book for a specific user.
     *
     * @param isbn ISBN of book to pin
     * @param userEmail Email of requesting user
     * @return BookResponseDTO of pinned book
     */
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
    // UNPIN BOOK
    // =====================================================
    /**
     * Removes a pinned book for a user.
     *
     * @param isbn Book ISBN
     * @param userEmail Email of requesting user
     */
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
    // VIEW PINNED BOOKS
    // =====================================================

    /**
     * Retrieves all books pinned by a specific user.
     *
     * @param userEmail Email of requesting user
     * @return List of pinned books
     */
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
    // SEARCH LOG RETRIEVAL
    // =====================================================

    /**
     * Retrieves search logs with optional filtering.
     *
     * @param keyword Search keyword
     * @param searchBy Field to filter (title, author, isbn)
     * @return List of SearchLogDTO
     */
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
                .map(log -> {

                    Book book = log.getBook();

                    SearchLogDTO dto = new SearchLogDTO();
                    dto.setId(log.getId());
                    dto.setIsbn(book.getIsbn());
                    dto.setTitle(book.getTitle());
                    dto.setAuthors(book.getAuthors());
                    dto.setCoverImageUrl(book.getCoverImageUrl());
                    dto.setPublicationYear(book.getPublicationYear());
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
    // DELETE SEARCH LOG (ADMIN ONLY)
    // =====================================================

    /**
     * Deletes a specific search log entry.
     *
     * Access Control:
     * - Only users with ADMIN role are allowed to perform this operation.
     *
     * Validation Process:
     * 1. Verify that the requesting user is an ADMIN.
     * 2. Check if the search log exists using its ID.
     * 3. Delete the search log if found.
     *
     * @param id ID of the search log to delete
     * @param userEmail Email of the requesting user
     *
     * @throws ResponseStatusException
     * - 400 BAD_REQUEST if userEmail is invalid
     * - 403 FORBIDDEN if user is not ADMIN
     * - 404 NOT_FOUND if search log does not exist
     */
    @Override
    public void deleteSearchLog(Long id, String userEmail) {

        checkAdmin(userEmail);

        SearchLog log = searchLogRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Search log not found"
                        ));

        searchLogRepository.delete(log);
    }

    // =====================================================
    // DELETE ALL SEARCH LOGS (ADMIN ONLY)
    // =====================================================

    /**
     * Deletes all search log records in the system,
     * except those associated with pinned books.
     *
     * Access Control:
     * - Only users with ADMIN role are allowed.
     *
     * Behavior:
     * - Validates ADMIN role.
     * - Calls a custom repository method that preserves
     *   logs related to pinned books.
     *
     * This ensures that important or bookmarked records
     * remain protected from bulk deletion.
     *
     * @param userEmail Email of the requesting user
     *
     * @throws ResponseStatusException
     * - 400 BAD_REQUEST if userEmail is invalid
     * - 403 FORBIDDEN if user is not ADMIN
     */
    @Override
    public void deleteAllSearchLogs(String userEmail) {

        checkAdmin(userEmail);

        searchLogRepository.deleteAllExceptPinned();
    }

    // =====================================================
    // GET BOOK BY ISBN
    // =====================================================

    /**
     * Retrieves a single book from the catalog using its ISBN.
     *
     * Behavior:
     * - Searches the BookRepository for a matching ISBN.
     * - Converts the Book entity into a BookResponseDTO.
     *
     * @param isbn ISBN of the book to retrieve
     * @return BookResponseDTO containing full book metadata
     *
     * @throws ResponseStatusException
     * - 404 NOT_FOUND if book does not exist in the catalog
     */
    @Override
    public BookResponseDTO getBookByIsbn(String isbn) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        return mapToDTO(book);
    }

    // =====================================================
    // ENTITY TO DTO MAPPING
    // =====================================================

    /**
     * Converts Book entity into BookResponseDTO.
     *
     * @param book Book entity
     * @return BookResponseDTO representation
     */
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