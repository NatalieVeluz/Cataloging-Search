package com.catalog.repository;

import com.catalog.entity.PinnedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PinnedBookRepository
        extends JpaRepository<PinnedBook, Long> {

    Optional<PinnedBook> findByBook_IdAndPinnedBy(
            Long bookId, String pinnedBy);

    List<PinnedBook> findByPinnedBy(String pinnedBy);

    void deleteByBook_IdAndPinnedBy(
            Long bookId, String pinnedBy);
}
