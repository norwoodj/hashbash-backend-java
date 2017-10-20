package com.johnmalcolmnorwood.hashbash.repository;

import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RainbowChainRepository extends JpaRepository<RainbowChain, String> {
    long countByRainbowTableId(short rainbowTableId);

    RainbowChain findByEndHashAndRainbowTableId(String endHash, short rainbowTableId);
}
