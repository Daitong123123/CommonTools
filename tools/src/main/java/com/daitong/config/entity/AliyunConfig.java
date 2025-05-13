package com.daitong.config.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aliyun")
public class AliyunConfig {
    private String accessKeyId;
    private String accessKeySecret;

    private String qwenApiKey;

    // getter/setter
    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getQwenApiKey() {
        return qwenApiKey;
    }

    public void setQwenApiKey(String qwenApiKey) {
        this.qwenApiKey = qwenApiKey;
    }
}
