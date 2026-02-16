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

@Service
public class LocServiceImpl implements LocService {

    private final RestTemplate restTemplate;

    public LocServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ======================================================
    // ISBN ENRICHMENT (PRIMARY)
    // ======================================================
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

                // Only include valid ISBN results
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
    // MARC PARSER (CLEAN & SAFE)
    // ======================================================
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

                    // ================= LCCN (010$a) =================
                    if ("010".equals(tag) && "a".equals(code)) {
                        book.setLccn(value.trim());
                        if (!sourceSet) {
                            book.setMetadataSource("Library of Congress");
                            sourceSet = true;
                        }
                    }

                    // ================= ISBN (020$a) =================
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

                    // ================= TITLE (245$a) =================
                    if ("245".equals(tag) && "a".equals(code)) {
                        book.setTitle(value.replaceAll("/$", "").trim());
                        if (!sourceSet) {
                            book.setMetadataSource("Library of Congress");
                            sourceSet = true;
                        }
                    }

                    // ================= MAIN AUTHOR (100$a) =================
                    if ("100".equals(tag) && "a".equals(code)) {
                        book.setAuthors(value.trim());
                        if (!sourceSet) {
                            book.setMetadataSource("Library of Congress");
                            sourceSet = true;
                        }
                    }

                    // ================= FALLBACK AUTHOR (700$a) =================
                    if ("700".equals(tag) && "a".equals(code)
                            && book.getAuthors() == null) {
                        book.setAuthors(value.trim());
                        if (!sourceSet) {
                            book.setMetadataSource("Library of Congress");
                            sourceSet = true;
                        }
                    }

                    // ================= PUBLICATION YEAR =================
                    if (("260".equals(tag) || "264".equals(tag)) && "c".equals(code)) {

                        String cleanedYear = value.replaceAll("[^0-9]", "");

                        if (cleanedYear.length() >= 4) {
                            book.setPublicationYear(cleanedYear.substring(0, 4));
                        }
                    }

                    // ================= EDITION (250$a) =================
                    if ("250".equals(tag) && "a".equals(code)) {
                        book.setEdition(value.trim());
                    }

                    // ================= PUBLISHER (260$b or 264$b) =================
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
