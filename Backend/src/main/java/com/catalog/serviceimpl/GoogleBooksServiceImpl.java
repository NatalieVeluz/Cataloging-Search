package com.catalog.serviceimpl;

import com.catalog.dto.BookResponseDTO;
import com.catalog.service.GoogleBooksService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleBooksServiceImpl implements GoogleBooksService {

    private final RestTemplate restTemplate;

    @Value("${google.books.api.key}")
    private String apiKey;

    public GoogleBooksServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // =====================================================
    // ISBN ENRICH
    // =====================================================
    @Override
    public void enrich(BookResponseDTO book) {

        if (book == null || book.getIsbn() == null) return;

        try {

            String encodedIsbn =
                    URLEncoder.encode(book.getIsbn(), StandardCharsets.UTF_8);

            String url =
                    "https://www.googleapis.com/books/v1/volumes?q=isbn:"
                            + encodedIsbn + "&key=" + apiKey;

            JsonNode root =
                    restTemplate.getForObject(url, JsonNode.class);

            if (root == null || !root.has("items") || root.get("items").isEmpty())
                return;

            JsonNode volumeInfo =
                    root.get("items").get(0).get("volumeInfo");

            extractFields(volumeInfo, book);

        } catch (Exception e) {
            System.out.println("Google Books ISBN Error: " + e.getMessage());
        }
    }

    // =====================================================
    // TITLE SEARCH
    // =====================================================
    @Override
    public List<BookResponseDTO> searchByTitle(String title) {

        List<BookResponseDTO> books = new ArrayList<>();

        try {

            String encodedTitle =
                    URLEncoder.encode(title, StandardCharsets.UTF_8);

            String url =
                    "https://www.googleapis.com/books/v1/volumes?q=intitle:"
                            + encodedTitle + "&maxResults=10&key=" + apiKey;

            JsonNode root =
                    restTemplate.getForObject(url, JsonNode.class);

            if (root == null || !root.has("items")) return books;

            for (JsonNode item : root.get("items")) {

                JsonNode volumeInfo = item.get("volumeInfo");
                if (volumeInfo == null) continue;

                BookResponseDTO book = new BookResponseDTO();
                extractFields(volumeInfo, book);

                if (book.getIsbn() != null) {
                    books.add(book);
                }
            }

        } catch (Exception e) {
            System.out.println("Google Books Title Search Error: " + e.getMessage());
        }

        return books;
    }

    // =====================================================
    // AUTHOR SEARCH
    // =====================================================
    @Override
    public List<BookResponseDTO> searchByAuthor(String author) {

        List<BookResponseDTO> books = new ArrayList<>();

        try {

            String encodedAuthor =
                    URLEncoder.encode(author, StandardCharsets.UTF_8);

            String url =
                    "https://www.googleapis.com/books/v1/volumes?q=inauthor:"
                            + encodedAuthor + "&maxResults=10&key=" + apiKey;

            JsonNode root =
                    restTemplate.getForObject(url, JsonNode.class);

            if (root == null || !root.has("items")) return books;

            for (JsonNode item : root.get("items")) {

                JsonNode volumeInfo = item.get("volumeInfo");
                if (volumeInfo == null) continue;

                BookResponseDTO book = new BookResponseDTO();
                extractFields(volumeInfo, book);

                if (book.getIsbn() != null) {
                    books.add(book);
                }
            }

        } catch (Exception e) {
            System.out.println("Google Books Author Search Error: " + e.getMessage());
        }

        return books;
    }

    // =====================================================
    // FIELD EXTRACTION (Reusable)
    // =====================================================
    private void extractFields(JsonNode volumeInfo, BookResponseDTO book) {

        if (volumeInfo == null) return;

        if (volumeInfo.hasNonNull("title"))
            book.setTitle(volumeInfo.get("title").asText());

        if (volumeInfo.has("authors")
                && volumeInfo.get("authors").isArray()
                && volumeInfo.get("authors").size() > 0)
            book.setAuthors(volumeInfo.get("authors").get(0).asText());

        if (volumeInfo.hasNonNull("publisher"))
            book.setPublisher(volumeInfo.get("publisher").asText());

        if (volumeInfo.hasNonNull("publishedDate")) {
            String date = volumeInfo.get("publishedDate").asText();
            String year = date.replaceAll("[^0-9]", "");
            if (year.length() >= 4)
                book.setPublicationYear(year.substring(0, 4));
        }

        if (volumeInfo.hasNonNull("description"))
            book.setSummary(volumeInfo.get("description").asText());

        if (volumeInfo.has("categories")
                && volumeInfo.get("categories").isArray()
                && volumeInfo.get("categories").size() > 0)
            book.setContentNotes(
                    volumeInfo.get("categories").get(0).asText()
            );

        if (volumeInfo.has("imageLinks")
                && volumeInfo.get("imageLinks").has("thumbnail"))
            book.setCoverImageUrl(
                    volumeInfo.get("imageLinks")
                            .get("thumbnail").asText()
            );

        // Extract valid ISBN
        if (volumeInfo.has("industryIdentifiers")) {
            for (JsonNode id : volumeInfo.get("industryIdentifiers")) {
                String identifier = id.get("identifier").asText();
                String cleaned = identifier.replaceAll("[^0-9X]", "");
                if (cleaned.matches("\\d{10}|\\d{13}")) {
                    book.setIsbn(cleaned);
                    break;
                }
            }
        }

        book.setMetadataSource("Google Books");
    }
}
