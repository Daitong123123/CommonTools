package com.daitong.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.daitong.bo.aliyunfile.CreateFileRequest;
import com.daitong.bo.aliyunfile.CreateFileResponse;
import com.daitong.bo.aliyunfile.PartInfo;
import com.daitong.utils.PdsSignatureUtils;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class AliyunCloudFileService {

    @Value("${aliyun.access.key.id:}")
    private String ACCESS_KEY_ID ;

    @Value("${aliyun.access.key.secret:}")
    private String ACCESS_KEY_SECRET ;

    private String host = "bj20871.api.aliyunpds.com";

    private String baseUrl = "https://bj20871.api.aliyunpds.com";

    private String domainId = "bj20871";

    private String DRIVER_ID= "1";


    private String createFilePath = "/v2/file/create";
    private String completeFilePath = "/v2/file/complete";

    private int chipSize = 1024*4;

    @Value("${spring.profiles.active:}")
    private String env ;

    public void uploadFile(MultipartFile file) {
        String fileName = "test";
        // 创建文件获取上传地址
        CreateFileRequest createFileRequest = new CreateFileRequest();
        createFileRequest.setName(fileName);
        createFileRequest.setDriveId(DRIVER_ID);
        createFileRequest.setParentFileId("root/dinner-images");
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
        HttpResponse createResponse = aliyunRequest(createFilePath, JSONObject.toJSONString(createFileRequest), "POST");
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

                    // 上传分片
                    RequestBody body = RequestBody.create(null, partContent);
                    Request request = new Request.Builder()
                            .url(uploadPartInfo.getUploadUrl())
                            .header("Content-Length", String.valueOf(size))
                            .put(body)
                            .build();

                    OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
                    try (Response response = okHttpClient.newCall(request).execute()) {
                        // 判断分片是否上传成功
                        if (!response.isSuccessful()) {
                            log.info("upload part failed, partNumber:" + number);
                        }
                        log.info("upload part success, partNumber:" + number);
                    }
                } catch (IOException e) {
                    log.info("upload part failed, partNumber:" + number);
                    log.error("fail upload ", e);
                }
            });
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upload_id", uploadId);
        jsonObject.put("file_id", fileId);
        jsonObject.put("drive_id",DRIVER_ID);
        // 调用完成接口 completeFilePath
        HttpResponse completeRes = aliyunRequest(completeFilePath, jsonObject.toJSONString(), "POST");
        log.info("completeRes:{}", completeRes);
    }

    private HttpResponse aliyunRequest(String path, String body, String method){
        String fullUrl = baseUrl + path;
        // 计算Content-MD5
        String contentMd5 = PdsSignatureUtils.calculateContentMd5(body);
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
                    ACCESS_KEY_ID,
                    ACCESS_KEY_SECRET,
                    method,
                    "application/json",
                    contentMd5,
                    "application/json; charset=UTF-8",
                    currentGmtDate,
                    headers,
                    path // 只传递路径部分
            );

            HttpRequest httpRequest = HttpRequest.post(fullUrl);
            if("dev".equals(env)){
                java.net.Proxy proxy = new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.huawei.com", 8080));
                httpRequest.setProxy(proxy);
            }

            httpRequest.header("Authorization", authorization);
            headers.forEach(httpRequest::header);
            httpRequest.body(body);

            return httpRequest.execute();
        }catch (Exception e){
            log.error("call aliyun file system fail", e);
            return null;
        }
    }


}