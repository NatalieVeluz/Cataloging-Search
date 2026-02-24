package com.catalog.serviceimpl;

import com.catalog.dto.BookResponseDTO;
import com.catalog.service.OpenLibraryService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenLibraryServiceImpl
 *
 * Concrete implementation of OpenLibraryService.
 *
 * This service integrates with the Open Library API
 * to retrieve bibliographic metadata.
 *
 * Responsibilities:
 * - Enrich book metadata using ISBN
 * - Search books by title
 * - Search books by author
 * - Extract relevant bibliographic fields
 *
 * API Strategy:
 * - First perform search query
 * - Retrieve edition key
 * - Fetch full edition JSON for detailed metadata
 *
 * Data Source:
 * - https://openlibrary.org/search.json
 * - https://openlibrary.org/books/{editionKey}.json
 */
@Service
public class OpenLibraryServiceImpl implements OpenLibraryService {

    private final RestTemplate restTemplate;

    /**
     * Constructor-based dependency injection.
     *
     * @param restTemplate Used for performing HTTP requests
     */
    public OpenLibraryServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ======================================================
    // ISBN ENRICHMENT
    // ======================================================

    /**
     * Enriches a BookResponseDTO using ISBN.
     *
     * Process:
     * 1. Search Open Library using ISBN.
     * 2. Extract first edition key.
     * 3. Fetch full edition JSON.
     * 4. Extract metadata fields such as:
     *      - Publisher
     *      - Edition
     *      - Publication Year
     *      - Subjects
     *      - Summary
     *      - Cover image
     *
     * @param book BookResponseDTO to enrich
     */
    @Override
    public void enrich(BookResponseDTO book) {

        if (book == null || book.getIsbn() == null) return;

        try {

            String encodedIsbn =
                    URLEncoder.encode(book.getIsbn(), StandardCharsets.UTF_8);

            // Step 1: Search by ISBN
            String searchUrl =
                    "https://openlibrary.org/search.json?isbn=" + encodedIsbn;

            JsonNode searchRoot =
                    restTemplate.getForObject(searchUrl, JsonNode.class);

            if (searchRoot == null
                    || !searchRoot.has("docs")
                    || searchRoot.get("docs").isEmpty()) return;

            JsonNode doc = searchRoot.get("docs").get(0);

            // Basic metadata from search result
            if (doc.hasNonNull("title"))
                book.setTitle(doc.get("title").asText());

            if (doc.has("author_name")
                    && doc.get("author_name").isArray()
                    && doc.get("author_name").size() > 0)
                book.setAuthors(doc.get("author_name").get(0).asText());

            // Step 2: Retrieve edition key
            if (!doc.has("edition_key")
                    || !doc.get("edition_key").isArray()
                    || doc.get("edition_key").isEmpty()) return;

            String editionKey =
                    doc.get("edition_key").get(0).asText();

            // Step 3: Retrieve edition metadata
            String editionUrl =
                    "https://openlibrary.org/books/" + editionKey + ".json";

            JsonNode editionRoot =
                    restTemplate.getForObject(editionUrl, JsonNode.class);

            if (editionRoot == null) return;

            // Publisher
            if (editionRoot.has("publishers")
                    && editionRoot.get("publishers").isArray()
                    && editionRoot.get("publishers").size() > 0) {

                book.setPublisher(
                        editionRoot.get("publishers").get(0).asText()
                );
            }

            // Edition name
            if (editionRoot.hasNonNull("edition_name"))
                book.setEdition(
                        editionRoot.get("edition_name").asText()
                );

            // Publication year
            if (editionRoot.hasNonNull("publish_date")) {

                String publishDate =
                        editionRoot.get("publish_date").asText();

                String year =
                        publishDate.replaceAll("[^0-9]", "");

                if (year.length() >= 4)
                    book.setPublicationYear(
                            year.substring(year.length() - 4)
                    );
            }

            // Page count
            if (editionRoot.hasNonNull("number_of_pages")) {
                book.setContentNotes(
                        "Pages: " +
                                editionRoot.get("number_of_pages").asText()
                );
            }

            // Summary
            if (editionRoot.has("notes")) {
                book.setSummary(
                        editionRoot.get("notes").asText()
                );
            }

            // Subjects (limited to first 5)
            if (editionRoot.has("subjects")
                    && editionRoot.get("subjects").isArray()) {

                StringBuilder subjects = new StringBuilder();

                for (int i = 0;
                     i < editionRoot.get("subjects").size() && i < 5;
                     i++) {

                    subjects.append(
                            editionRoot.get("subjects").get(i).asText()
                    ).append(", ");
                }

                if (subjects.length() > 2)
                    book.setContentNotes(
                            subjects.substring(0,
                                    subjects.length() - 2)
                    );
            }

            // Cover image
            if (editionRoot.has("covers")
                    && editionRoot.get("covers").isArray()
                    && editionRoot.get("covers").size() > 0) {

                String coverId =
                        editionRoot.get("covers").get(0).asText();

                book.setCoverImageUrl(
                        "https://covers.openlibrary.org/b/id/"
                                + coverId + "-L.jpg"
                );
            }

            book.setMetadataSource("OpenLibrary");

        } catch (Exception e) {
            System.out.println("OpenLibrary Error: " + e.getMessage());
        }
    }

    // ======================================================
    // TITLE SEARCH
    // ======================================================

    /**
     * Searches Open Library by title.
     *
     * Only books with valid ISBN (10 or 13 digits) are returned.
     * Maximum of 10 results.
     *
     * @param title Title keyword
     * @return List of BookResponseDTO
     */
    @Override
    public List<BookResponseDTO> searchByTitle(String title) {

        List<BookResponseDTO> books = new ArrayList<>();

        try {

            String encodedTitle =
                    URLEncoder.encode(title, StandardCharsets.UTF_8);

            String url =
                    "https://openlibrary.org/search.json?title=" + encodedTitle;

            JsonNode root =
                    restTemplate.getForObject(url, JsonNode.class);

            if (root == null || !root.has("docs")) return books;

            for (JsonNode doc : root.get("docs")) {

                BookResponseDTO book = new BookResponseDTO();

                if (doc.has("isbn")) {
                    for (JsonNode isbnNode : doc.get("isbn")) {

                        String cleaned =
                                isbnNode.asText()
                                        .replaceAll("[^0-9X]", "");

                        if (cleaned.matches("\\d{10}|\\d{13}")) {
                            book.setIsbn(cleaned);
                            break;
                        }
                    }
                }

                if (book.getIsbn() == null) continue;

                book.setTitle(doc.get("title").asText());

                if (doc.has("author_name")
                        && doc.get("author_name").size() > 0)
                    book.setAuthors(
                            doc.get("author_name").get(0).asText()
                    );

                book.setMetadataSource("OpenLibrary");

                books.add(book);

                if (books.size() == 10) break;
            }

        } catch (Exception e) {
            System.out.println("OpenLibrary Title Search Error: " + e.getMessage());
        }

        return books;
    }

    // ======================================================
    // AUTHOR SEARCH
    // ======================================================

    /**
     * Searches Open Library by author.
     *
     * Only books with valid ISBN are returned.
     * Maximum of 10 results.
     *
     * @param author Author keyword
     * @return List of BookResponseDTO
     */
    @Override
    public List<BookResponseDTO> searchByAuthor(String author) {

        List<BookResponseDTO> books = new ArrayList<>();

        try {

            String encodedAuthor =
                    URLEncoder.encode(author, StandardCharsets.UTF_8);

            String url =
                    "https://openlibrary.org/search.json?author=" + encodedAuthor;

            JsonNode root =
                    restTemplate.getForObject(url, JsonNode.class);

            if (root == null || !root.has("docs")) return books;

            for (JsonNode doc : root.get("docs")) {

                BookResponseDTO book = new BookResponseDTO();

                if (doc.has("isbn")) {
                    for (JsonNode isbnNode : doc.get("isbn")) {

                        String cleaned =
                                isbnNode.asText()
                                        .replaceAll("[^0-9X]", "");

                        if (cleaned.matches("\\d{10}|\\d{13}")) {
                            book.setIsbn(cleaned);
                            break;
                        }
                    }
                }

                if (book.getIsbn() == null) continue;

                book.setTitle(doc.get("title").asText());

                if (doc.has("author_name")
                        && doc.get("author_name").size() > 0)
                    book.setAuthors(
                            doc.get("author_name").get(0).asText()
                    );

                book.setMetadataSource("OpenLibrary");

                books.add(book);

                if (books.size() == 10) break;
            }

        } catch (Exception e) {
            System.out.println("OpenLibrary Author Search Error: " + e.getMessage());
        }

        return books;
    }
}