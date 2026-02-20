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

@Service
public class OpenLibraryServiceImpl implements OpenLibraryService {

    private final RestTemplate restTemplate;

    public OpenLibraryServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ======================================================
    // ISBN ENRICH (SEARCH → EDITION → FULL METADATA)
    // ======================================================
    @Override
    public void enrich(BookResponseDTO book) {

        if (book == null || book.getIsbn() == null) return;

        try {
            String encodedIsbn =
                    URLEncoder.encode(book.getIsbn(), StandardCharsets.UTF_8);

            // 1️⃣ SEARCH
            String searchUrl =
                    "https://openlibrary.org/search.json?isbn=" + encodedIsbn;

            JsonNode searchRoot =
                    restTemplate.getForObject(searchUrl, JsonNode.class);

            if (searchRoot == null
                    || !searchRoot.has("docs")
                    || searchRoot.get("docs").isEmpty()) return;

            JsonNode doc = searchRoot.get("docs").get(0);

            // BASIC FIELDS
            if (doc.hasNonNull("title"))
                book.setTitle(doc.get("title").asText());

            if (doc.has("author_name")
                    && doc.get("author_name").isArray()
                    && doc.get("author_name").size() > 0)
                book.setAuthors(doc.get("author_name").get(0).asText());

            // 2️⃣ EDITION KEY
            if (!doc.has("edition_key")
                    || !doc.get("edition_key").isArray()
                    || doc.get("edition_key").isEmpty()) return;

            String editionKey =
                    doc.get("edition_key").get(0).asText();

            // 3️⃣ EDITION JSON
            String editionUrl =
                    "https://openlibrary.org/books/" + editionKey + ".json";

            JsonNode editionRoot =
                    restTemplate.getForObject(editionUrl, JsonNode.class);

            if (editionRoot == null) return;

            // ================= PUBLISHER =================
            if (editionRoot.has("publishers")
                    && editionRoot.get("publishers").isArray()
                    && editionRoot.get("publishers").size() > 0) {

                book.setPublisher(
                        editionRoot.get("publishers").get(0).asText()
                );
            }

            // ================= EDITION =================
            if (editionRoot.hasNonNull("edition_name"))
                book.setEdition(
                        editionRoot.get("edition_name").asText()
                );

            // ================= PUBLICATION YEAR =================
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

            // ================= PAGE COUNT =================
            if (editionRoot.hasNonNull("number_of_pages")) {
                book.setContentNotes(
                        "Pages: " +
                                editionRoot.get("number_of_pages").asText()
                );
            }

            // ================= SUMMARY =================
            if (editionRoot.has("notes")) {
                book.setSummary(
                        editionRoot.get("notes").asText()
                );
            }

            // ================= SUBJECTS =================
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

            // ================= COVER =================
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

        } catch (Exception ignored) {}

        return books;
    }

    // ======================================================
    // AUTHOR SEARCH
    // ======================================================
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

        } catch (Exception ignored) {}

        return books;
    }
}