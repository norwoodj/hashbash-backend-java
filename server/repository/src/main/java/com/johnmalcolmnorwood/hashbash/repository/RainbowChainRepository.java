package com.johnmalcolmnorwood.hashbash.repository;

import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RainbowChainRepository extends JpaRepository<RainbowChain, String> {
    long countByRainbowTableId(short rainbowTableId);

    RainbowChain findByEndHashAndRainbowTableId(String endHash, short rainbowTableId);

    List<RainbowChain> findByRainbowTableIdAndEndHashIn(short rainbowTableId, List<String> endHashes);
}
