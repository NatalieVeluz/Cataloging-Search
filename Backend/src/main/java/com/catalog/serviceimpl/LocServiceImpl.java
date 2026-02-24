package com.catalog.serviceimpl;

import com.catalog.dto.BookResponseDTO;
import com.catalog.service.LocService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * LocServiceImpl
 *
 * Concrete implementation of LocService.
 *
 * This service integrates with the Library of Congress (LOC) API
 * to retrieve authoritative bibliographic metadata.
 *
 * Responsibilities:
 * - Enrich book metadata using ISBN
 * - Search books by title
 * - Search books by author
 * - Retrieve and parse MARCXML records
 *
 * Data Source:
 * - LOC JSON search endpoint
 * - LOC MARCXML detailed record endpoint
 *
 * Parsing Strategy:
 * - Fetch JSON search result
 * - Extract item ID
 * - Retrieve MARCXML record
 * - Parse relevant MARC fields
 */
@Service
public class LocServiceImpl implements LocService {

    private final RestTemplate restTemplate;

    /**
     * Constructor-based dependency injection.
     *
     * @param restTemplate Used for performing HTTP requests
     */
    public LocServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ======================================================
    // ISBN ENRICHMENT
    // ======================================================

    /**
     * Enriches an existing BookResponseDTO using ISBN.
     *
     * Process:
     * 1. Search LOC using ISBN (JSON endpoint).
     * 2. Retrieve item ID.
     * 3. Fetch MARCXML record.
     * 4. Parse relevant MARC fields.
     *
     * @param book BookResponseDTO to enrich
     */
    @Override
    public void enrich(BookResponseDTO book) {

        if (book == null || book.getIsbn() == null) return;

        try {

            String encodedIsbn =
                    URLEncoder.encode(book.getIsbn(), StandardCharsets.UTF_8);

            String searchUrl =
                    "https://www.loc.gov/books/?q=isbn:" + encodedIsbn + "&fo=json";

            JsonNode root = restTemplate.getForObject(searchUrl, JsonNode.class);

            if (root == null || !root.has("results") || root.get("results").isEmpty())
                return;

            JsonNode result = root.get("results").get(0);

            if (!result.has("item") || !result.get("item").has("@id"))
                return;

            String itemId = result.get("item").get("@id").asText();

            parseMarc(itemId + "marcxml", book);

        } catch (Exception e) {
            System.out.println("LOC ISBN Search Error: " + e.getMessage());
        }
    }

    // ======================================================
    // TITLE SEARCH
    // ======================================================

    /**
     * Searches books in LOC by title.
     *
     * Limits results to a maximum of 10 records.
     * Only records with valid ISBN are included.
     *
     * @param title Title keyword
     * @return List of BookResponseDTO
     */
    @Override
    public List<BookResponseDTO> searchByTitle(String title) {

        List<BookResponseDTO> books = new ArrayList<>();
        if (title == null || title.isBlank()) return books;

        try {

            String encodedTitle =
                    URLEncoder.encode(title, StandardCharsets.UTF_8);

            String searchUrl =
                    "https://www.loc.gov/books/?q=title:" + encodedTitle + "&fo=json";

            JsonNode root = restTemplate.getForObject(searchUrl, JsonNode.class);
            if (root == null || !root.has("results")) return books;

            for (JsonNode result : root.get("results")) {

                if (!result.has("item") || !result.get("item").has("@id"))
                    continue;

                String itemId = result.get("item").get("@id").asText();

                BookResponseDTO book = new BookResponseDTO();
                parseMarc(itemId + "marcxml", book);

                if (book.getIsbn() != null) {
                    books.add(book);
                }

                if (books.size() == 10) break;
            }

        } catch (Exception e) {
            System.out.println("LOC Title Search Error: " + e.getMessage());
        }

        return books;
    }

    // ======================================================
    // AUTHOR SEARCH
    // ======================================================

    /**
     * Searches books in LOC by author name.
     *
     * Limits results to 10 valid ISBN records.
     *
     * @param author Author keyword
     * @return List of BookResponseDTO
     */
    @Override
    public List<BookResponseDTO> searchByAuthor(String author) {

        List<BookResponseDTO> books = new ArrayList<>();
        if (author == null || author.isBlank()) return books;

        try {

            String encodedAuthor =
                    URLEncoder.encode(author, StandardCharsets.UTF_8);

            String searchUrl =
                    "https://www.loc.gov/books/?q=creator:" + encodedAuthor + "&fo=json";

            JsonNode root = restTemplate.getForObject(searchUrl, JsonNode.class);
            if (root == null || !root.has("results")) return books;

            for (JsonNode result : root.get("results")) {

                if (!result.has("item") || !result.get("item").has("@id"))
                    continue;

                String itemId = result.get("item").get("@id").asText();

                BookResponseDTO book = new BookResponseDTO();
                parseMarc(itemId + "marcxml", book);

                if (book.getIsbn() != null) {
                    books.add(book);
                }

                if (books.size() == 10) break;
            }

        } catch (Exception e) {
            System.out.println("LOC Author Search Error: " + e.getMessage());
        }

        return books;
    }

    // ======================================================
    // MARCXML PARSING
    // ======================================================

    /**
     * Parses MARCXML record retrieved from LOC.
     *
     * Extracted MARC Fields:
     * - 010$a → LCCN
     * - 020$a → ISBN
     * - 245$a → Title
     * - 100$a → Main Author
     * - 700$a → Secondary Author (fallback)
     * - 260$c / 264$c → Publication Year
     * - 250$a → Edition
     * - 260$b / 264$b → Publisher
     *
     * @param marcUrl MARCXML endpoint URL
     * @param book BookResponseDTO to populate
     */
    private void parseMarc(String marcUrl, BookResponseDTO book) {

        try {

            String marcXml = restTemplate.getForObject(marcUrl, String.class);
            if (marcXml == null) return;

            Document doc = DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(marcXml.getBytes()));

            NodeList dataFields = doc.getElementsByTagName("datafield");

            boolean sourceSet = false;

            for (int i = 0; i < dataFields.getLength(); i++) {

                Element field = (Element) dataFields.item(i);
                String tag = field.getAttribute("tag");

                NodeList subfields = field.getElementsByTagName("subfield");

                for (int j = 0; j < subfields.getLength(); j++) {

                    Element sub = (Element) subfields.item(j);
                    String code = sub.getAttribute("code");
                    String value = sub.getTextContent().trim();

                    if (value.isBlank()) continue;

                    // LCCN (010$a)
                    if ("010".equals(tag) && "a".equals(code)) {
                        book.setLccn(value.trim());
                        if (!sourceSet) {
                            book.setMetadataSource("Library of Congress");
                            sourceSet = true;
                        }
                    }

                    // ISBN (020$a)
                    if ("020".equals(tag) && "a".equals(code)) {
                        String cleaned = value.replaceAll("[^0-9X]", "");
                        if (cleaned.matches("\\d{10}|\\d{13}")) {
                            book.setIsbn(cleaned);
                            if (!sourceSet) {
                                book.setMetadataSource("Library of Congress");
                                sourceSet = true;
                            }
                        }
                    }

                    // Title (245$a)
                    if ("245".equals(tag) && "a".equals(code)) {
                        book.setTitle(value.replaceAll("/$", "").trim());
                        if (!sourceSet) {
                            book.setMetadataSource("Library of Congress");
                            sourceSet = true;
                        }
                    }

                    // Main Author (100$a)
                    if ("100".equals(tag) && "a".equals(code)) {
                        book.setAuthors(value.trim());
                        if (!sourceSet) {
                            book.setMetadataSource("Library of Congress");
                            sourceSet = true;
                        }
                    }

                    // Fallback Author (700$a)
                    if ("700".equals(tag) && "a".equals(code)
                            && book.getAuthors() == null) {
                        book.setAuthors(value.trim());
                    }

                    // Publication Year (260$c or 264$c)
                    if (("260".equals(tag) || "264".equals(tag)) && "c".equals(code)) {

                        String cleanedYear = value.replaceAll("[^0-9]", "");

                        if (cleanedYear.length() >= 4) {
                            book.setPublicationYear(cleanedYear.substring(0, 4));
                        }
                    }

                    // Edition (250$a)
                    if ("250".equals(tag) && "a".equals(code)) {
                        book.setEdition(value.trim());
                    }

                    // Publisher (260$b or 264$b)
                    if (("260".equals(tag) || "264".equals(tag)) && "b".equals(code)) {
                        book.setPublisher(value.replaceAll(",$", "").trim());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("LOC MARC Parse Error: " + e.getMessage());
        }
    }
}