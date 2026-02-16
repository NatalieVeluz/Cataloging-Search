package com.catalog.dto;

public class BookResponseDTO {

    private String isbn;
    private String title;
    private String authors;
    private String lccn;
    private String cutterNumber;
    private String coverImageUrl;
    private String publicationYear;
    private String edition;
    private String publisher;
    private String summary;
    private String contentNotes;
    private String metadataSource;

    // 🔥 NEW FIELDS

    // ================= GETTERS & SETTERS =================

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

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getContentNotes() { return contentNotes; }
    public void setContentNotes(String contentNotes) { this.contentNotes = contentNotes; }

    public String getMetadataSource() { return metadataSource; }
    public void setMetadataSource(String metadataSource) { this.metadataSource = metadataSource; }

    // 🔥 NEW
    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

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
