package com.capstone.bowlingbling.domain.community.repository;

import com.capstone.bowlingbling.domain.community.domain.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Page<Community> findByCategory(String category, Pageable pageable);
}
