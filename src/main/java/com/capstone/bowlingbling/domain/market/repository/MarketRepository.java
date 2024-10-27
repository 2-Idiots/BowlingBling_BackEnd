package com.capstone.bowlingbling.domain.market.repository;

import com.capstone.bowlingbling.domain.market.domain.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    @Modifying
    @Query("UPDATE Market m SET m.title = :title, m.contents = :contents, m.sales = :sales, m.images = :images WHERE m.id = :id")
    void updateMarket(@Param("id") Long id, @Param("title") String title, @Param("contents") String contents,
                      @Param("sales") int sales, @Param("images") List<String> images);
}