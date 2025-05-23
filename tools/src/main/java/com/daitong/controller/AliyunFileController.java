package com.daitong.controller;

import com.daitong.bo.aliyunfile.AliyunDownloadResponse;
import com.daitong.bo.common.BaseResponse;
import com.daitong.repository.FileManagerRepository;
import com.daitong.repository.entity.FileManager;
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

    @Autowired
    private FileManagerRepository fileManagerRepository;

    @PostMapping("/aliyun/upload")
    public BaseResponse aliyunUpload(@RequestParam("file") MultipartFile file) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setData(aliyunCloudFileService.uploadFile(file));
            baseResponse.setCode("200");
            baseResponse.setMessage("上传成功");
            return baseResponse;
        } catch (Exception e) {
            baseResponse.setCode("500");
            return baseResponse;
        }
    }



    @GetMapping("/aliyun/download")
    public AliyunDownloadResponse aliyunDownload(String fileId) {
        AliyunDownloadResponse aliyunDownloadResponse = new AliyunDownloadResponse();
        try {
            String data = aliyunCloudFileService.downloadFile(fileId);
            aliyunDownloadResponse.setData(data);
            aliyunDownloadResponse.setCode("200");
            aliyunDownloadResponse.setMessage("ok");
            return aliyunDownloadResponse;
        } catch (Exception e) {
            aliyunDownloadResponse.setCode("500");
            aliyunDownloadResponse.setMessage(e.getMessage());
            return aliyunDownloadResponse;
        }
    }

    @GetMapping("/aliyun/fileExist")
    public BaseResponse aliyunFileExist(String hash) {
        BaseResponse baseResponse = new BaseResponse();
        String data = null;
        try {
            FileManager fileManager = fileManagerRepository.getByHash(hash);
            if (fileManager != null) {
                data = fileManager.getFileId();
            }
            baseResponse.setData(data);
            baseResponse.setCode("200");
            baseResponse.setMessage("ok");
            return baseResponse;
        } catch (Exception e) {
            baseResponse.setCode("500");
            baseResponse.setMessage(e.getMessage());
            return baseResponse;
        }
    }
}
