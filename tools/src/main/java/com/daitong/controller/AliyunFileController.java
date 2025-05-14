package com.daitong.controller;

import com.daitong.bo.aliyunfile.AliyunDownloadResponse;
import com.daitong.bo.common.BaseResponse;
import com.daitong.service.AliyunCloudFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AliyunFileController {

    @Autowired
    private AliyunCloudFileService aliyunCloudFileService;

    @PostMapping("/aliyun/upload")
    public BaseResponse aliyunUpload(@RequestParam("file") MultipartFile file)  {
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

    @GetMapping ("/aliyun/download")
    public AliyunDownloadResponse aliyunDownload(String fileId) {
        AliyunDownloadResponse aliyunDownloadResponse = new AliyunDownloadResponse();
        try {
            aliyunDownloadResponse.setData(aliyunCloudFileService.downloadFile(fileId));
            aliyunDownloadResponse.setCode("200");
            aliyunDownloadResponse.setMessage("ok");
            return aliyunDownloadResponse;
        } catch (Exception e) {
            aliyunDownloadResponse.setCode("500");
            aliyunDownloadResponse.setMessage(e.getMessage());
            return aliyunDownloadResponse;
        }
    }
}
