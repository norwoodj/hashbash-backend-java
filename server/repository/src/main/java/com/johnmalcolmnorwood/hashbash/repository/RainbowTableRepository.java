package com.johnmalcolmnorwood.hashbash.repository;

import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RainbowTableRepository extends PagingAndSortingRepository<RainbowTable, Short> {
}
