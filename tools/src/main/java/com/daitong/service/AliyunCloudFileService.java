package com.daitong.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.daitong.bo.aliyunfile.CreateFileRequest;
import com.daitong.bo.aliyunfile.CreateFileResponse;
import com.daitong.bo.aliyunfile.PartInfo;
import com.daitong.config.entity.AliyunConfig;
import com.daitong.utils.PdsSignatureUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class AliyunCloudFileService {


    @Autowired
    private AliyunConfig aliyunConfig;

    private String host = "bj20871.api.aliyunpds.com";

    private String baseUrl = "https://bj20871.api.aliyunpds.com";

    private String dataHost = "4568851b6c434d6a958dd485e6553221.data.aliyunpds.com";

    private String domainId = "bj20871";

    private String DRIVER_ID = "1";


    private String createFilePath = "/v2/file/create";
    private String completeFilePath = "/v2/file/complete";

    private String parentFileId = "6821bf91192bf2a2276449aba1ed31c8f153c2b6";

    private int chipSize = 1024 * 4 * 1024;

    @Value("${spring.profiles.active:}")
    private String env;

    public void uploadFile(MultipartFile file) {
        String fileName = "test";
        // 创建文件获取上传地址
        CreateFileRequest createFileRequest = new CreateFileRequest();
        createFileRequest.setName(fileName);
        createFileRequest.setDriveId(DRIVER_ID);
        createFileRequest.setParentFileId(parentFileId);
        createFileRequest.setType("file");
        createFileRequest.setSize(file.getSize());
        int chipCount = (int) (file.getSize() % chipSize == 0 ? file.getSize() / chipSize : (file.getSize() / chipSize + 1));
        List<PartInfo> list = new ArrayList<>();
        for (int i = 1; i <= chipCount; i++) {
            PartInfo partInfo = new PartInfo();
            partInfo.setPartNumber(i);
            list.add(partInfo);
        }
        String uploadId = null;
        String fileId = null;
        createFileRequest.setPartInfoList(list);
        List<PartInfo> partInfoList = null;
        HttpResponse createResponse = aliyunRequest(baseUrl,createFilePath, JSONObject.toJSONString(createFileRequest), "POST", null);
        if (createResponse != null && createResponse.getStatus() == 201) {
            CreateFileResponse createFileResponse = JSONObject.parseObject(createResponse.body(), CreateFileResponse.class);
            partInfoList = createFileResponse.getPartInfoList();
            uploadId = createFileResponse.getUploadId();
            fileId = createFileResponse.getFileId();
        }
        // 上传文件
        if (CollectionUtils.isNotEmpty(partInfoList)) {
            partInfoList.forEach(uploadPartInfo -> {
                // 计算分片在文件中的位置
                int number = uploadPartInfo.getPartNumber();
                long pos = (number - 1) * chipSize;
                long size = Math.min(file.getSize() - pos, chipSize);
                byte[] partContent = new byte[(int) size];

                try (InputStream inputStream = file.getInputStream()) {
                    // 跳过前面的字节
                    inputStream.skip(pos);
                    // 读取分片内容
                    int bytesRead = 0;
                    int offset = 0;
                    while (offset < size && (bytesRead = inputStream.read(partContent, offset, (int) (size - offset))) != -1) {
                        offset += bytesRead;
                    }

//                    // 创建Hutool的HttpRequest并配置代理
//                    HttpRequest httpRequest = HttpRequest.put(uploadPartInfo.getUploadUrl())
//                            .header("Content-Length", String.valueOf(size))
//                            .body(partContent);
//
//                    // 根据环境配置代理
//                    if ("dev".equals(env)) {
//                        httpRequest.setProxy(
//                                new java.net.Proxy(
//                                        Proxy.Type.HTTP,
//                                        new InetSocketAddress("proxy.huawei.com", 8080)
//                                )
//                        );
//                    }
//                    String contentMd5 = PdsSignatureUtils.calculateContentMd5(new ByteArrayInputStream(partContent));
//                    httpRequest.header("Content-MD5", contentMd5);
//                    httpRequest.header("Content-Type", "application/json; charset=UTF-8");
//                    httpRequest.header("Host",dataHost);
                    // 执行请求
//                    HttpResponse response = httpRequest.execute();
                    HttpResponse response = aliyunRequest("https://"+dataHost, uploadPartInfo.getUploadUrl().replace("https://"+dataHost,""),null,"PUT",partContent);
                    // 判断分片是否上传成功
                    if (!response.isOk()) {
                        log.error("upload part failed, partNumber:" + number);
                        log.error("Response: " + response.body());
                    } else {
                        log.info("upload part success, partNumber:" + number);
                    }
                } catch (IOException e) {
                    log.error("upload part failed, partNumber:" + number, e);
                }
            });
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upload_id", uploadId);
        jsonObject.put("file_id", fileId);
        jsonObject.put("drive_id", DRIVER_ID);
        // 调用完成接口 completeFilePath
        HttpResponse completeRes = aliyunRequest(baseUrl,completeFilePath, jsonObject.toJSONString(), "POST", null);
        log.info("completeRes:{}", completeRes);
    }

    private HttpResponse aliyunRequest(String rootUrl,String path, String body, String method, byte[] bodyBytes) {
        String fullUrl = rootUrl + path;
        // 计算Content-MD5
        String contentMd5 = null;
        if(body!=null){
            contentMd5 = PdsSignatureUtils.calculateContentMd5(body);
        }else{
            try{
                contentMd5 = PdsSignatureUtils.calculateContentMd5(new ByteArrayInputStream(bodyBytes));
            }catch (Exception e){
                log.error("calculateContentMd5 error",e);
            }
        }
        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put("Host", host); // 确保包含Host头
        headers.put("Content-MD5", contentMd5);
        // 生成并设置Date头
        String currentGmtDate = PdsSignatureUtils.getCurrentGmtDate();
        headers.put("Date", currentGmtDate);

        try {
            // 注意：这里传递的是listPath，而不是fullUrl
            String authorization = PdsSignatureUtils.generateAuthorization(
                    aliyunConfig.getAccessKeyId(),
                    aliyunConfig.getAccessKeySecret(),
                    method,
                    "application/json",
                    contentMd5,
                    "application/json; charset=UTF-8",
                    currentGmtDate,
                    headers,
                    path // 只传递路径部分
            );
            HttpRequest httpRequest = null;
            if("PUT".equalsIgnoreCase(method)){
                httpRequest = HttpRequest.put(fullUrl);
            }else if("GET".equalsIgnoreCase(method)){
                httpRequest = HttpRequest.get(fullUrl);
            }else if("POST".equalsIgnoreCase(method)){
                httpRequest = HttpRequest.post(fullUrl);
            }

            if ("dev".equals(env)) {
                java.net.Proxy proxy = new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.huawei.com", 8080));
                httpRequest.setProxy(proxy);
            }

            httpRequest.header("Authorization", authorization);
            headers.forEach(httpRequest::header);
            if(body == null){
                httpRequest.body(bodyBytes);
            }else{
                httpRequest.body(body);
            }
            return httpRequest.execute();
        } catch (Exception e) {
            log.error("call aliyun file system fail", e);
            return null;
        }
    }


}