package com.ht.repository;

import com.ht.entity.HouseDetail;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface HouseDetailRepository extends CrudRepository<HouseDetail, Long> {
    HouseDetail findByHouseId(Long id);
}