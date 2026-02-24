package com.catalog.dto;

/**
 * BookResponseDTO
 *
 * This Data Transfer Object (DTO) is used to transfer
 * book-related data between the backend and frontend.
 *
 * It represents the complete metadata of a book,
 * including bibliographic details and enrichment
 * information retrieved from external APIs.
 */
public class BookResponseDTO {

    // Core bibliographic fields
    private String isbn;
    private String title;
    private String authors;
    private String lccn;
    private String cutterNumber;

    // Publication and edition details
    private String publicationYear;
    private String edition;
    private String publisher;

    // Additional metadata
    private String coverImageUrl;
    private String summary;
    private String contentNotes;
    private String metadataSource;

    // ================= GETTERS AND SETTERS =================

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public String getLccn() { return lccn; }
    public void setLccn(String lccn) { this.lccn = lccn; }

    public String getCutterNumber() { return cutterNumber; }
    public void setCutterNumber(String cutterNumber) { this.cutterNumber = cutterNumber; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getPublicationYear() { return publicationYear; }
    public void setPublicationYear(String publicationYear) { this.publicationYear = publicationYear; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContentNotes() { return contentNotes; }
    public void setContentNotes(String contentNotes) { this.contentNotes = contentNotes; }

    public String getMetadataSource() { return metadataSource; }
    public void setMetadataSource(String metadataSource) { this.metadataSource = metadataSource; }

    /**
     * Copies all field values from another BookResponseDTO object.
     *
     * This method is useful when updating existing book metadata
     * or synchronizing data between objects.
     *
     * @param other another BookResponseDTO instance
     */
    public void copyFrom(BookResponseDTO other) {
        if (other == null) return;

        this.isbn = other.isbn;
        this.title = other.title;
        this.authors = other.authors;
        this.lccn = other.lccn;
        this.cutterNumber = other.cutterNumber;
        this.coverImageUrl = other.coverImageUrl;
        this.publicationYear = other.publicationYear;
        this.edition = other.edition;
        this.publisher = other.publisher;
        this.summary = other.summary;
        this.contentNotes = other.contentNotes;
        this.metadataSource = other.metadataSource;
    }
}