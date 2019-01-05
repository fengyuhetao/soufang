package com.ht.repository;

import com.ht.entity.HouseDetail;
import com.ht.entity.HousePicture;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface HousePictureRepository extends CrudRepository<HousePicture, Long> {
    List<HousePicture> findAllByHouseId(Long id);
}
