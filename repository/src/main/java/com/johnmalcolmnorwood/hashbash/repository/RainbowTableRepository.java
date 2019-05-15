package com.johnmalcolmnorwood.hashbash.repository;

import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
public interface RainbowTableRepository extends PagingAndSortingRepository<RainbowTable, Short> {

    @Transactional
    @Modifying
    @Query(
            "UPDATE RainbowTable r " +
            "SET r.chainsGenerated = :numChains " +
            "WHERE r.id = :rainbowTableId"
    )
    void setChainsGeneratedById(short rainbowTableId, long numChains);

    @Transactional
    @Modifying
    @Query(
            "UPDATE RainbowTable r " +
            "SET r.status = :status, r.generateStarted = CURRENT_TIME " +
            "WHERE r.id = :rainbowTableId"
    )
    void setStatusAndSearchStartedById(short rainbowTableId, String status);

    @Transactional
    @Modifying
    @Query(
            "UPDATE RainbowTable r " +
            "SET r.finalChainCount = :finalChainCount, r.status = :status, r.generateCompleted = CURRENT_TIME " +
            "WHERE r.id = :rainbowTableId"
    )
    void setStatusAndFinalChainCountById(short rainbowTableId, long finalChainCount, String status);
}
