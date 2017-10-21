package com.johnmalcolmnorwood.hashbash.repository;

import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearch;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearchStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;


@Repository
public interface RainbowTableSearchRepository extends PagingAndSortingRepository<RainbowTableSearch, Long> {

    List<RainbowTableSearch> getAllByRainbowTableId(short rainbowTableId, Pageable pageable);
    List<RainbowTableSearch> getAllByRainbowTableIdAndStatusIn(
            short rainbowTableId,
            Set<RainbowTableSearchStatus> status,
            Pageable pageable
    );

    @Query("UPDATE RainbowTableSearch s SET s.password = :password, s.status = :status WHERE s.id = :id")
    @Modifying
    @Transactional
    void updatePasswordAndStatusById(
            @Param("id") long id,
            @Param("password") String password,
            @Param("status") RainbowTableSearchStatus status
    );
}
