package com.capstone.bowlingbling.domain.comment.repository;

import com.capstone.bowlingbling.domain.comment.domain.MarketComment;
import com.capstone.bowlingbling.domain.market.domain.Market;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketCommentRepository extends JpaRepository<MarketComment, Long> {
    Page<MarketComment> findByMarket(Market market, Pageable pageable);
}
