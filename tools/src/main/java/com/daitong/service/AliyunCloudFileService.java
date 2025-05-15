package com.daitong.service;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.daitong.bo.aliyunfile.CreateFileRequest;
import com.daitong.bo.aliyunfile.CreateFileResponse;
import com.daitong.bo.aliyunfile.PartInfo;
import com.daitong.config.entity.AliyunConfig;
import com.daitong.constants.AliyunHeaders;
import com.daitong.exception.BaseException;
import com.daitong.manager.IdManager;
import com.daitong.repository.FileManagerRepository;
import com.daitong.repository.entity.FileManager;
import com.daitong.utils.AliyunRequestService;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class AliyunCloudFileService {



    @Autowired
    private AliyunRequestService aliyunRequestService;

    @Autowired
    private FileManagerRepository fileManagerRepository;

    private String host = "bj20871.api.aliyunpds.com";

    private String baseUrl = "https://bj20871.api.aliyunpds.com";

    private String dataHost = "4568851b6c434d6a958dd485e6553221.data.aliyunpds.com";

    private String domainId = "bj20871";

    private String DRIVER_ID = "1";

    private String createFilePath = "/v2/file/create";

    private String completeFilePath = "/v2/file/complete";

    private String downloadPath = "/v2/file/download";

    private String parentFileId = "6821bf91192bf2a2276449aba1ed31c8f153c2b6";

    private long chipSize = 1024 * 4 * 1024;

    @Value("${spring.profiles.active:}")
    private String env;


    public String uploadFile(MultipartFile file) {
        List<PartInfo> partInfoList = null;
        String uploadId = null;
        String fileId = null;
        CreateFileRequest createFileRequest = buildCreateFileRequest(file);
        FileManager fileManager = fileManagerRepository.getByHash(createFileRequest.getContentHash());
        if(fileManager != null){
            return fileManager.getFileId();
        }
        HttpResponse response = aliyunRequestService.post(baseUrl + createFilePath, new HashMap<>(), JSONObject.toJSONString(createFileRequest));
        if (response != null && response.getStatus() == 201) {
            CreateFileResponse createFileResponse = JSONObject.parseObject(response.body(), CreateFileResponse.class);
            if (Boolean.TRUE.equals(createFileResponse.getRapidUpload())) {
                fileManagerRepository.save(buildFileManager(file.getSize(), createFileRequest.getContentHash(), createFileResponse.getFileId()));
                return createFileResponse.getFileId();
            }
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
                // 从 MultipartFile 中读取分片内容
                InputStream inputStream = null;
                ByteArrayOutputStream outputStream = null;
                try {
                    inputStream = file.getInputStream();
                    outputStream = new ByteArrayOutputStream();
                    // 跳过前面的字节
                    long skipped = 0;
                    while (skipped < pos) {
                        long curSkipped = inputStream.skip(pos - skipped);
                        if (curSkipped == 0) break;
                        skipped += curSkipped;
                    }

                    // 读取分片内容
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalRead = 0;
                    while (totalRead < size && (bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, size - totalRead))) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                    }
                    byte[] partContent = outputStream.toByteArray();
                    // 创建信任所有证书的 OkHttpClient
                    OkHttpClient okHttpClient = createTrustAllClient();
                    // 上传分片
                    RequestBody body = RequestBody.create(partContent, null);
                    Request request = new Request.Builder()
                            .url(uploadPartInfo.getUploadUrl())
                            .header("Content-Length", String.valueOf(size))
                            .put(body)
                            .build();
                    Response res = okHttpClient.newCall(request).execute();
                    // 判断分片是否上传成功
                    if (!res.isSuccessful()) {
                        String errorMsg = "上传分片失败, partNumber:" + number + ", 错误信息: " + res.body().string();
                        log.error(errorMsg);
                        return;
                    }
                    log.info("上传分片成功, partNumber: {}", number);
                } catch (IOException e) {
                    log.error("处理分片 {} 时发生IO异常", number, e);
                    throw new BaseException("500", "处理分片" + number + "时发生IO异常");
                } finally {
                    // 关闭资源
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            log.warn("关闭输出流失败", e);
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            log.warn("关闭输入流失败", e);
                        }
                    }
                }
            });
        }
        // 调用完成接口 completeFilePath
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upload_id", uploadId);
        jsonObject.put("file_id", fileId);
        jsonObject.put("drive_id", DRIVER_ID);
        HttpResponse completeRes = aliyunRequestService.post(baseUrl + completeFilePath, new HashMap<>(), jsonObject.toJSONString());
        log.info("completeRes:{}", completeRes);
        if (completeRes != null && completeRes.getStatus() != 200) {
            log.info("complete upload file fail.");
            throw new BaseException("500", "complete upload file fail.");
        }
        fileManagerRepository.save(buildFileManager(file.getSize(), createFileRequest.getContentHash(), fileId));
        return fileId;
    }

    private FileManager buildFileManager(long fileSize, String hash, String fileId){
        FileManager fileManager = new FileManager();
        fileManager.setId(IdManager.getId());
        fileManager.setFileSize(fileSize/1024.0+"kb");
        fileManager.setFileType("image");
        fileManager.setContentHash(hash);
        fileManager.setFileId(fileId);
        fileManager.setCreatedAt(new Date());
        fileManager.setUpdatedAt(new Date());
        fileManager.setDownloadUrl(downloadFile(fileId));
        return fileManager;
    }

    public String downloadFile(String fileId) {
        UrlBuilder urlBuilder = UrlBuilder.of(baseUrl + downloadPath);
        urlBuilder.addQuery("file_id", fileId);
        urlBuilder.addQuery("drive_id", DRIVER_ID);
        Map<String, String> header = new HashMap<>();
        header.put(AliyunHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        HttpResponse response = aliyunRequestService.get(urlBuilder.build(), header);
        if (response != null && response.getStatus() == 200) {
            return response.body();
        } else if (response != null && response.getStatus() == 302) {
//            HttpResponse redirectRes = aliyunRequestService.getDataNoSign(response.header("Location"));
//            if(redirectRes.isOk()){
//                return redirectRes.bodyBytes();
//            }
            return response.header("Location");
        }
        return null;
    }

    public String getFileHash(MultipartFile file) {
        try {
            return Hex.encodeHexString(getFileHashBytes(file));
        } catch (Exception e) {
            log.error("get content hash fail.", e);
            return null;
        }
    }

    private CreateFileRequest buildCreateFileRequest(MultipartFile file) {
        String fileName = file.getName();
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
        createFileRequest.setPartInfoList(list);
        String hash = getFileHash(file);
        if (StringUtils.isNotEmpty(hash)) {
            createFileRequest.setContentHash(hash);
            createFileRequest.setContentHashName("sha1");
        }
        return createFileRequest;
    }

    public byte[] getFileHashBytes(MultipartFile file) throws IOException {
        byte[] sha1;
        try (InputStream is = file.getInputStream()) { // 使用MultipartFile的输入流
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            byte[] buffer = new byte[10 * 1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            sha1 = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA1 algorithm not found.", e);
        }
        return sha1;
    }

    private OkHttpClient createTrustAllClient() {
        try {
            // 创建信任所有证书的 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // 安装信任管理器
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // 创建信任所有主机名的 HostnameVerifier
            HostnameVerifier allHostsValid = (hostname, session) -> true;

            // 配置 OkHttpClient
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(allHostsValid);

            // 根据环境决定是否使用代理
            if ("dev".equals(env)) {
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.huawei.com", 8080));
                builder.proxy(proxy);
            }

            return builder.build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("创建信任所有证书的OkHttpClient失败", e);
            // 返回默认客户端
            return new OkHttpClient();
        }
    }
}