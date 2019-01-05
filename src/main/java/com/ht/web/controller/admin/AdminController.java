package com.ht.web.controller.admin;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.ht.base.ApiDataTableResponse;
import com.ht.base.ApiResponse;
import com.ht.entity.HouseDetail;
import com.ht.enums.LevelEnum;
import com.ht.enums.StatusEnum;
import com.ht.form.DatatableSearch;
import com.ht.form.HouseForm;
import com.ht.service.*;
import com.ht.web.dto.*;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import jdk.internal.util.xml.impl.Input;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;

/**
 * @author HT
 * @version V1.0
 * @package com.ht.web.controller.admin
 * @date 2018-06-27 19:17
 */
@Controller
public class AdminController {
    @Autowired
    private IQiNiuService qiNiuService;

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private Gson gson;

    @GetMapping("/admin/center")
    public String adminCenterPage() {
        return "admin/center";
    }

    @GetMapping("/admin/welcome")
    public String welcomePage() {
        return "admin/welcome";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @GetMapping("admin/house/list")
    public String adminHouseList() {
        return "admin/house-list";
    }

    @GetMapping("/admin/add/house")
    public String adminAddHouse() {
        return "admin/house-add";
    }

    @PostMapping(value = "admin/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
        if(file.isEmpty()) {
            return ApiResponse.ofStatus(StatusEnum.NOT_VALID_PARAM);
        }

//        String fileName = file.getOriginalFilename();

        //        保存到七牛
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            Response response = this.qiNiuService.uploadFile(inputStream);
            if (response.isOK()) {
                QiNiuPutRet ret = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
                return ApiResponse.ofSuccess(ret);
            } else {
                return new ApiResponse(response.statusCode, response.getInfo());
            }
        } catch (QiniuException e1) {
            Response response = e1.response;
            try {
                return ApiResponse.ofMessage(response.statusCode, response.bodyString());
            } catch (QiniuException e) {
                e.printStackTrace();
                return ApiResponse.ofStatus(StatusEnum.INTERNAL_SERVER_ERROR);

            }
        } catch (IOException e) {
            return  ApiResponse.ofStatus(StatusEnum.INTERNAL_SERVER_ERROR);
        }

//        保存到本地
/*        File target = new File("D:\\java\\soufang\\tmp");
        try {
            file.transferTo(target);
        } catch (IOException e) {
            e.printStackTrace();
            return  ApiResponse.ofStatus(StatusEnum.INTERNAL_SERVER_ERROR);
        }*/
    }

    @PostMapping("admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }

        if(houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
        }

        Map<LevelEnum, SupportAddressDTO> addressDTOMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());

        if(addressDTOMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(StatusEnum.NOT_VALID_PARAM);
        }
        ServiceResult<HouseDTO> houseDTO =  houseService.save(houseForm);

        if(houseDTO.isSuccess()) {
            return ApiResponse.ofSuccess(houseDTO.getResult());
        }

        return ApiResponse.ofStatus(StatusEnum.NOT_VALID_PARAM);
    }

    @PostMapping("admin/houses")
    @ResponseBody
    public ApiDataTableResponse houses(@ModelAttribute DatatableSearch searchBody) {
        ServiceMultiResult<HouseDTO> result = houseService.adminQuery(searchBody);
        ApiDataTableResponse response = new ApiDataTableResponse(StatusEnum.SUCCESS);
        response.setData(result.getResult());
        response.setRecordsFiltered(result.getTotal());
        response.setRecordsTotal(result.getTotal());
        response.setDraw(searchBody.getDraw());
        return response;
    }

    @GetMapping("admin/house/edit")
    public String houseEditPage(@RequestParam(value = "id") Long id, Model model) {
        if(id == null || id < 1) {
            return "404";
        }

        ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(id);
        if(!serviceResult.isSuccess()) {
            return "404";
        }

        HouseDTO houseDTO = serviceResult.getResult();
        model.addAttribute("house", houseDTO);

        Map<LevelEnum, SupportAddressDTO> addressDTOMap = addressService.findCityAndRegion(houseDTO.getCityEnName(), houseDTO.getRegionEnName());
        model.addAttribute("city", addressDTOMap.get(LevelEnum.CITY));
        model.addAttribute("region", addressDTOMap.get(LevelEnum.REGION));

        HouseDetailDTO detailDTO = houseDTO.getHouseDetail();
        ServiceResult<SubwayDTO> subwayDTOServiceResult = addressService.findSubway(detailDTO.getSubwayLineId());
        if(subwayDTOServiceResult.isSuccess()) {
            model.addAttribute("subway", subwayDTOServiceResult.getResult());
        }

        ServiceResult<SubwayStationDTO> subwayStationDTOServiceResult = addressService.findSubwayStation(detailDTO.getSubwayStationId());
        if(subwayStationDTOServiceResult.isSuccess()) {
            model.addAttribute("station", subwayStationDTOServiceResult.getResult());
        }
        return "admin/house-edit";
    }


    /**
     * 编辑接口
     */
    @PostMapping("admin/house/edit")
    @ResponseBody
    public ApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0), null);
        }

        Map<LevelEnum, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());

        if(addressMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(StatusEnum.NOT_VALID_PARAM);
        }

        ServiceResult result = houseService.update(houseForm);
        if(result.isSuccess()) {
            return ApiResponse.ofSuccess(null);
        }

        ApiResponse response = ApiResponse.ofStatus(StatusEnum.BAD_REQUEST);
        response.setMessage(response.getMessage());
        return response;
    }

    /**
     * 移除图片接口
     * @param id
     * @return
     */
    @DeleteMapping("admin/house/photo")
    @ResponseBody
    public ApiResponse removeHousePhoto(@RequestParam(value = "id") Long id) {
        ServiceResult result = this.houseService.removePhoto(id);

        if(result.isSuccess()) {
            return ApiResponse.ofStatus(StatusEnum.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 修改封面接口
     */
    @PostMapping("admin/house/cover")
    @ResponseBody
    public ApiResponse updateCover(@RequestParam(value = "cover_id") Long coverId,
                                   @RequestParam(value = "target_id") Long targetId) {
        ServiceResult result = this.houseService.updateCover(coverId, targetId);

        if (result.isSuccess()) {
            return ApiResponse.ofStatus(StatusEnum.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 增加标签接口
     * @param houseId
     * @param tag
     * @return
     */
    @PostMapping("admin/house/tag")
    @ResponseBody
    public ApiResponse addHouseTag(@RequestParam(value = "house_id") Long houseId,
                                   @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ApiResponse.ofStatus(StatusEnum.BAD_REQUEST);
        }

        ServiceResult result = this.houseService.addTag(houseId, tag);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(StatusEnum.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }

    /**
     * 移除标签接口
     * @param houseId
     * @param tag
     * @return
     */
    @DeleteMapping("admin/house/tag")
    @ResponseBody
    public ApiResponse removeHouseTag(@RequestParam(value = "house_id") Long houseId,
                                      @RequestParam(value = "tag") String tag) {
        if (houseId < 1 || Strings.isNullOrEmpty(tag)) {
            return ApiResponse.ofStatus(StatusEnum.BAD_REQUEST);
        }

        ServiceResult result = this.houseService.removeTag(houseId, tag);
        if (result.isSuccess()) {
            return ApiResponse.ofStatus(StatusEnum.SUCCESS);
        } else {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
        }
    }
}
