package com.catalog.dto;

import java.time.LocalDateTime;

public class SearchLogDTO {

    private Long id;

    private String isbn;
    private String title;
    private String authors;
    private String coverImageUrl;
    private String publicationYear;

    // 🔥 FULL METADATA FIELDS
    private String lccn;
    private String cutterNumber;
    private String edition;
    private String publisher;
    private String summary;
    private String contentNotes;
    private String metadataSource;

    private LocalDateTime searchedAt;

    // ================= GETTERS & SETTERS =================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getPublicationYear() { return publicationYear; }
    public void setPublicationYear(String publicationYear) { this.publicationYear = publicationYear; }

    public String getLccn() { return lccn; }
    public void setLccn(String lccn) { this.lccn = lccn; }

    public String getCutterNumber() { return cutterNumber; }
    public void setCutterNumber(String cutterNumber) { this.cutterNumber = cutterNumber; }

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

    public LocalDateTime getSearchedAt() { return searchedAt; }
    public void setSearchedAt(LocalDateTime searchedAt) { this.searchedAt = searchedAt; }
}
