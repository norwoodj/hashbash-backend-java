package com.johnmalcolmnorwood.hashbash.repository;

import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearch;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearchStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface RainbowTableSearchRepository extends PagingAndSortingRepository<RainbowTableSearch, Long> {

    @Query("UPDATE RainbowTableSearch s SET s.password = :password, s.status = :status WHERE s.id = :id")
    @Modifying
    @Transactional
    void updatePasswordAndStatusById(
            @Param("id") long id,
            @Param("password") String password,
            @Param("status") RainbowTableSearchStatus status
    );
}
