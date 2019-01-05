package com.ht.service;

import com.ht.form.DatatableSearch;
import com.ht.form.HouseForm;
import com.ht.web.dto.HouseDTO;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.service
 * @date 2018-07-07 16:49
 */
public interface IHouseService {
    ServiceResult<HouseDTO> save(HouseForm form);

    ServiceResult<HouseDTO> update(HouseForm houseForm);

    ServiceMultiResult<HouseDTO> adminQuery(DatatableSearch searchBody);

    ServiceResult<HouseDTO> findCompleteOne(Long id);

    /**
     * 移除图片
     * @param id
     * @return
     */
    ServiceResult removePhoto(Long id);

    ServiceResult updateCover(Long coverId, Long targetId);

    ServiceResult addTag(Long houseId, String tag);

    ServiceResult removeTag(Long houseId, String tag);
}
