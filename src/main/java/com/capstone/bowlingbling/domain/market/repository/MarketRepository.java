package com.capstone.bowlingbling.domain.market.repository;

import com.capstone.bowlingbling.domain.market.domain.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
}