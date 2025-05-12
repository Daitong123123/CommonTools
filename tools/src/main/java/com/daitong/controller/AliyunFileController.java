package com.daitong.controller;

import com.daitong.bo.common.BaseResponse;
import com.daitong.service.AliyunCloudFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AliyunFileController {

    @Autowired
    private AliyunCloudFileService aliyunCloudFileService;

    @PostMapping("/aliyun/upload")
    public BaseResponse aliyun(@RequestParam("file") MultipartFile file) throws Exception {
        BaseResponse baseResponse = new BaseResponse();
        try {
            aliyunCloudFileService.uploadFile(file);
            baseResponse.setCode("200");
            baseResponse.setMessage("ok");
            baseResponse.setData("上传成功");
            return baseResponse;
        } catch (Exception e) {
            baseResponse.setCode("500");
            return baseResponse;
        }
    }
}
