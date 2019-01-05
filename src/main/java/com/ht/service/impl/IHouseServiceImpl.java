package com.ht.service.impl;

import com.ht.base.LoginUserUtil;
import com.ht.entity.*;
import com.ht.enums.HouseStatusEnum;
import com.ht.form.DatatableSearch;
import com.ht.form.HouseForm;
import com.ht.form.PhotoForm;
import com.ht.repository.*;
import com.ht.service.IHouseService;
import com.ht.service.ServiceMultiResult;
import com.ht.service.ServiceResult;
import com.ht.web.dto.HouseDTO;
import com.ht.web.dto.HouseDetailDTO;
import com.ht.web.dto.HousePictureDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.service.impl
 * @date 2018-07-07 16:53
 */
@Service
public class IHouseServiceImpl implements IHouseService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;

    @Override
    public ServiceResult<HouseDTO> save(HouseForm form) {
        HouseDetail houseDetail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidationResult = wrapperHouseDetailInfo(houseDetail, form);
        if(subwayValidationResult != null) {
            return subwayValidationResult;
        }

        House house = new House();
        modelMapper.map(form, house);

        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        house = houseRepository.save(house);

        houseDetail.setHouseId(house.getId());
        houseDetail = houseDetailRepository.save(houseDetail);

        List<HousePicture> pictureList = generatePictures(form, house.getId());
        Iterable<HousePicture> housePictures = housePictureRepository.saveAll(pictureList);

        HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail, HouseDetailDTO.class);

        houseDTO.setHouseDetail(houseDetailDTO);
        List<HousePictureDTO> pictureDTOS = new ArrayList<>();
        housePictures.forEach(housePicture -> pictureDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setCover(this.cdnPrefix + "/" + houseDTO.getCover());
        List<String> tags = form.getTags();
        if(tags != null && !tags.isEmpty()) {
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag : tags) {
                houseTags.add(new HouseTag(house.getId(), tag));
            }
            houseTagRepository.saveAll(houseTags);
            houseDTO.setTags(tags);
        }
        return new ServiceResult<HouseDTO>(true, null, houseDTO);
    }

    private List<HousePicture> generatePictures(HouseForm houseForm, Long houseId) {
        List<HousePicture> pictureList = new ArrayList<>();
        if(houseForm.getPhotos() == null || houseForm.getPhotos().isEmpty()) {
            return pictureList;
        }

        for (PhotoForm photoForm : houseForm.getPhotos()) {
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setCdnPrefix(cdnPrefix);
            picture.setPath(photoForm.getPath());
            picture.setWidth(photoForm.getWidth());
            picture.setHeight(photoForm.getHeight());
            pictureList.add(picture);
        }
        return pictureList;
    }

    private ServiceResult<HouseDTO> wrapperHouseDetailInfo(HouseDetail houseDetail, HouseForm houseForm) {
        Optional<Subway> opSubway = subwayRepository.findById(houseForm.getSubwayLineId());
        if(!opSubway.isPresent()) {
            return new ServiceResult<HouseDTO>(false, "Not valid subway line!");
        }

        Optional<SubwayStation> subwayStation = subwayStationRepository.findById(houseForm.getSubwayStationId());

        if(!subwayStation.isPresent() || !Objects.equals(opSubway.get().getId(), subwayStation.get().getSubwayId())) {
            return new ServiceResult<HouseDTO>(false, "Not valid subway station!");
        }

        houseDetail.setSubwayLineId(opSubway.get().getId());
        houseDetail.setSubwayLineName(opSubway.get().getName());
        houseDetail.setSubwayStationId(subwayStation.get().getId());
        houseDetail.setSubwayStationName(subwayStation.get().getName());

        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setDetailAddress(houseForm.getDetailAddress());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
        return null;
    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody) {
        List<HouseDTO> houseDTOS = new ArrayList<>();
        System.out.println(searchBody);
        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength();
        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);
        Specification<House> specification = (root, query, cb) -> {
            Predicate predicate = cb.equal(root.get("adminId"), LoginUserUtil.getLoginUserId());
            System.out.println("deleted:" + HouseStatusEnum.DELETED.getValue());
            predicate = cb.and(predicate, cb.notEqual(root.get("status"), HouseStatusEnum.DELETED.getValue()));

            if(searchBody.getCity() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("cityEnName"), searchBody.getCity()));
            }

            if(searchBody.getStatus() != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), searchBody.getStatus()));
            }

            if(searchBody.getCreateTimeMin() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMin()));
            }

            if(searchBody.getCreateTimeMax() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createTime"), searchBody.getCreateTimeMax()));
            }

            if(searchBody.getTitle() != null) {
                predicate = cb.and(predicate, cb.like(root.get("title"), "%" + searchBody.getTitle() + "%"));
            }
            return predicate;
        };

        Page<House> houses = houseRepository.findAll(specification, pageable);
        houses.forEach(house -> {
            HouseDTO houseDTO = modelMapper.map(house, HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix + '/' + house.getCover());
            houseDTOS.add(houseDTO);
        });

        return new ServiceMultiResult<HouseDTO>(houses.getTotalElements(), houseDTOS);
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Long id) {
        Optional<House> house = houseRepository.findById(id);
        if(!house.isPresent()) {
            return ServiceResult.notFound();
        }

        HouseDetail detail= houseDetailRepository.findByHouseId(id);
        List<HousePicture> pictures = housePictureRepository.findAllByHouseId(id);
        HouseDetailDTO detailDTO = modelMapper.map(detail, HouseDetailDTO.class);

        List<HousePictureDTO> pictureDTOS = new ArrayList<>();

        for(HousePicture picture: pictures) {
            HousePictureDTO pictureDTO = modelMapper.map(picture, HousePictureDTO.class);
            pictureDTOS.add(pictureDTO);
        }

        List<HouseTag> tags = houseTagRepository.findAllById(id);
        List<String> tagList = new ArrayList<>();
        for(HouseTag houseTag: tags) {
            tagList.add(houseTag.getName());
        }

        HouseDTO houseDTO = modelMapper.map(house.get(), HouseDTO.class);
        houseDTO.setHouseDetail(detailDTO);
        houseDTO.setPictures(pictureDTOS);
        houseDTO.setTags(tagList);

        return ServiceResult.of(houseDTO);
    }

    @Override
    @Transactional
    public ServiceResult<HouseDTO> update(HouseForm houseForm) {
        Optional<House> house = this.houseRepository.findById(houseForm.getId());
        if(!house.isPresent()) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = this.houseDetailRepository.findByHouseId(house.get().getId());
        if(detail == null) {
            return ServiceResult.notFound();
        }

        ServiceResult wrapperResult = wrapperHouseDetailInfo(detail, houseForm);
        if(wrapperResult != null) {
            return wrapperResult;
        }

        houseDetailRepository.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        housePictureRepository.saveAll(pictures);

        if(houseForm.getCover() == null) {
            houseForm.setCover(house.get().getCover());
        }

        modelMapper.map(houseForm, house.get());
        house.get().setLastUpdateTime(new Date());
        houseRepository.save(house.get());
        return ServiceResult.success();
    }

    @Override
    public ServiceResult removePhoto(Long id) {
        return null;
    }

    @Override
    public ServiceResult updateCover(Long coverId, Long targetId) {
        return null;
    }

    @Override
    public ServiceResult addTag(Long houseId, String tag) {
        return null;
    }

    @Override
    public ServiceResult removeTag(Long houseId, String tag) {
        return null;
    }
}
