package com.daitong.utils;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.daitong.config.entity.AliyunConfig;
import com.daitong.constants.AliyunHeaders;
import com.daitong.exception.BaseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;

@Service
@Log4j2
public class AliyunRequestService {

    @Autowired
    private AliyunConfig aliyunConfig;

    @Value("${spring.profiles.active:}")
    private String env;

    public HttpResponse get(String url, Map<String, String> headers){
        UrlBuilder urlBuilder = UrlBuilder.of(url);
        setCommonHeadersToSign(urlBuilder.getHost(), headers);
        String authorization = getAuthorization("GET", headers, getPathToSign(urlBuilder));
        setAuthorization(authorization, headers);
        HttpRequest httpRequest = HttpRequest.get(url);
        setProxy(httpRequest);
        headers.forEach(httpRequest::header);
        try{
            return httpRequest.execute();
        }catch (Exception e){
            log.error("httpRequest fail:",e);
            throw new BaseException("500", "httpRequest fail:url"+url+"."+e.getMessage());
        }
    }

    public HttpResponse post(String url, Map<String, String> headers, String body){
        UrlBuilder urlBuilder = UrlBuilder.of(url);
        setCommonHeadersToSign(urlBuilder.getHost(), headers);
        setContentMd5ToSign(body, headers);
        String authorization = getAuthorization("POST", headers, getPathToSign(urlBuilder));
        setAuthorization(authorization, headers);
        HttpRequest httpRequest = HttpRequest.post(url);
        httpRequest.body(body);
        setProxy(httpRequest);
        headers.forEach(httpRequest::header);
        try{
            return httpRequest.execute();
        }catch (Exception e){
            log.error("httpRequest fail:",e);
            throw new BaseException("500", "httpRequest fail:url"+url+"."+e.getMessage());
        }
    }

    public HttpResponse getDataNoSign(String url){
        HttpRequest httpRequest = HttpRequest.get(url);
        setProxy(httpRequest);
        try{
            return httpRequest.execute();
        }catch (Exception e){
            log.error("httpRequest fail:",e);
            throw new BaseException("500", "httpRequest fail:url"+url+"."+e.getMessage());
        }
    }

    private String getPathToSign(UrlBuilder urlBuilder){
        if(StringUtils.isBlank(urlBuilder.getQueryStr())){
            return urlBuilder.getPathStr();
        }
        return urlBuilder.getPathStr()+"?"+urlBuilder.getQueryStr();
    }

    private void setProxy(HttpRequest httpRequest){
        if ("dev".equals(env)) {
            java.net.Proxy proxy = new java.net.Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.huawei.com", 8080));
            httpRequest.setProxy(proxy);
        }
    }

    private void setCommonHeadersToSign(String host,Map<String,String> headers){
        headers.putIfAbsent(AliyunHeaders.ACCEPT, "application/json");
        headers.putIfAbsent(AliyunHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
        headers.putIfAbsent(AliyunHeaders.HOST, host); // 确保包含Host头
        // 生成并设置Date头
        String currentGmtDate = PdsSignatureUtils.getCurrentGmtDate();
        headers.putIfAbsent(AliyunHeaders.DATE, currentGmtDate);
    }

    private void setContentMd5ToSign(String body,Map<String,String> headers){
       String contentMd5 = PdsSignatureUtils.calculateContentMd5(body);
       headers.put(AliyunHeaders.CONTENT_MD5, contentMd5);
    }

    private void setAuthorization(String authorization,Map<String,String> headers){
        headers.put(AliyunHeaders.AUTHORIZATION, authorization);
    }

    private void setContentMd5ToSign(byte[] body,Map<String,String> headers){
        try {
            String contentMd5 = PdsSignatureUtils.calculateContentMd5(new ByteArrayInputStream(body));
            headers.put(AliyunHeaders.CONTENT_MD5, contentMd5);
        }catch (Exception e){
            log.error("setContentMd5ToSign fail[byte]:",e);
            throw new BaseException("500", "setContentMd5ToSign fail[byte]:"+e.getMessage());
        }
    }

    private String getAuthorization(String method, Map<String,String> headers, String pathToSign){
        try{
            return PdsSignatureUtils.generateAuthorization(
                    aliyunConfig.getAccessKeyId(),
                    aliyunConfig.getAccessKeySecret(),
                    method,
                    headers,
                    pathToSign // 只传递路径部分
            );
        }catch (Exception e){
            log.error("generateAuthorization fail:",e);
            throw new BaseException("500", "generateAuthorization fail:"+e.getMessage());
        }

    }
}
