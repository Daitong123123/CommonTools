package com.daitong.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daitong.bo.common.BasePageResponse;
import com.daitong.bo.common.BaseResponse;
import com.daitong.bo.common.CommonResponse;
import com.daitong.bo.common.PageRequest;
import com.daitong.repository.ModelConfigRepository;
import com.daitong.repository.entity.ModelConfig;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class ModelConfigController {

    @Autowired
    private ModelConfigRepository modelConfigRepository;

    @GetMapping("/admin/model-type-list")
    public BaseResponse modelTypeList() {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setCode("200");
            baseResponse.setMessage("请求成功");
            List<String> modelTypeList = modelConfigRepository.selectObjs("model_type").stream().map(String::valueOf).collect(Collectors.toList());
            baseResponse.setData(modelTypeList);
            return baseResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            baseResponse.setCode("500");
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @GetMapping("/admin/model-list-by-type")
    public BaseResponse modelListByType(String modelType) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setCode("200");
            baseResponse.setMessage("请求成功");
            List<ModelConfig> list = modelConfigRepository.list(new QueryWrapper<ModelConfig>().lambda().eq(ModelConfig::getModelType, modelType));
            baseResponse.setData(list);
            return baseResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            baseResponse.setCode("500");
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @GetMapping("/admin/model-current")
    public BaseResponse modelCurrent() {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setCode("200");
            baseResponse.setMessage("请求成功");
            ModelConfig modelConfig = modelConfigRepository.getOne(new QueryWrapper<ModelConfig>().lambda().eq(ModelConfig::isSelected, true));
            baseResponse.setData(modelConfig);
            return baseResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            baseResponse.setCode("500");
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @GetMapping("/admin/switch-model")
    public BaseResponse modelSwitch(@RequestParam String modelType, @RequestParam String model) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setCode("200");
            baseResponse.setMessage("请求成功");
            ModelConfig oldConfig = modelConfigRepository.getOne(new LambdaQueryWrapper<ModelConfig>().eq(ModelConfig::isSelected, true));
            oldConfig.setSelected(false);
            modelConfigRepository.save(oldConfig);
            ModelConfig config = modelConfigRepository.getOne(new LambdaQueryWrapper<ModelConfig>().eq(ModelConfig::getModelType, modelType).eq(ModelConfig::getModel, model));
            config.setSelected(true);
            modelConfigRepository.save(config);
            return baseResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            baseResponse.setCode("500");
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @PostMapping("/admin/add-model")
    public BaseResponse modelAdd(@RequestBody ModelConfig config) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setCode("200");
            baseResponse.setMessage("请求成功");
            config.setSelected(false);
            config.setUpdatedAt(new Date());
            config.setUpdatedAt(new Date());
            modelConfigRepository.save(config);
            return baseResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            baseResponse.setCode("500");
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @PostMapping("/admin/update-model")
    public BaseResponse modelUpdate(@RequestBody ModelConfig config) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setCode("200");
            baseResponse.setMessage("请求成功");
            config.setUpdatedAt(new Date());
            config.setUpdatedAt(new Date());
            modelConfigRepository.saveOrUpdate(config);
            return baseResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            baseResponse.setCode("500");
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }

    @PostMapping("/admin/model-list")
    public BasePageResponse modelList(@RequestBody PageRequest pageRequest) {
        BasePageResponse basePageResponse = new BasePageResponse();
        try {
            basePageResponse.setCode("200");
            basePageResponse.setMessage("请求成功");
            Page<ModelConfig> page = new Page<>(pageRequest.getCurPage(), pageRequest.getPageSize());
            modelConfigRepository.page(page);
            basePageResponse.setTotal((int) page.getTotal());
            basePageResponse.setData(page.getRecords());
            return basePageResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            basePageResponse.setCode("500");
            basePageResponse.setMessage(e.getMessage());
        }
        return basePageResponse;
    }

    @PostMapping("/admin/delete-model")
    public CommonResponse modelDelete(@RequestBody List<Long> deleteList) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setCode("200");
            baseResponse.setMessage("请求成功");
            modelConfigRepository.removeByIds(deleteList);
            return baseResponse;
        } catch (Exception e) {
            log.error("请求失败", e);
            baseResponse.setCode("500");
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }
}
