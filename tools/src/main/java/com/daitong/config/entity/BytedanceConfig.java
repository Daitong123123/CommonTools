package com.daitong.config.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bytedance")
public class BytedanceConfig {

    public String getDoubaoApiKey() {
        return doubaoApiKey;
    }

    public void setDoubaoApiKey(String doubaoApiKey) {
        this.doubaoApiKey = doubaoApiKey;
    }

    private String doubaoApiKey;
}
