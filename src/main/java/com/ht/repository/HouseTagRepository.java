package com.ht.repository;

import com.ht.entity.House;
import com.ht.entity.HouseTag;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HouseTagRepository extends CrudRepository<HouseTag, Long> {
    List<HouseTag> findAllById(Long id);
}
