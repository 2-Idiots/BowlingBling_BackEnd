package com.capstone.bowlingbling.domain.community.repository;

import com.capstone.bowlingbling.domain.community.domain.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Page<Community> findByCategory(String category, Pageable pageable);

    @Modifying
    @Query("UPDATE Community c SET " +
            "c.title = COALESCE(:title, c.title), " +
            "c.contents = COALESCE(:contents, c.contents), " +
            "c.category = COALESCE(:category, c.category) " +
            "WHERE c.id = :id")
    void updateCommunityInfo(@Param("id") Long id, @Param("title") String title, @Param("contents") String contents, @Param("category") String category);
}
