package com.catalog.repository;

import com.catalog.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {

    List<SearchLog> findAllByOrderBySearchedAtDesc();

    List<SearchLog> findByBook_TitleContainingIgnoreCaseOrderBySearchedAtDesc(String keyword);

    List<SearchLog> findByBook_AuthorsContainingIgnoreCaseOrderBySearchedAtDesc(String keyword);

    List<SearchLog> findByBook_IsbnContainingOrderBySearchedAtDesc(String keyword);

    // 🔥 DELETE ALL EXCEPT PINNED BOOK LOGS
    @Transactional
    @Modifying
    @Query("""
        DELETE FROM SearchLog s
        WHERE s.book.id NOT IN (
            SELECT p.book.id FROM PinnedBook p
        )
    """)
    void deleteAllExceptPinned();
}
