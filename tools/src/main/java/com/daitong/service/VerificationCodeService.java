package com.daitong.service;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * VerificationCodeService
 *
 * @since 2025-04-11
 */
@Service
@Log4j2
public class VerificationCodeService {
    private static final int CODE_LENGTH = 4;
    private static final long CODE_EXPIRATION_TIME = 5; // 验证码有效期为 5 分钟

    private final Map<String, String> verificationCodes = new HashMap<>();
    private final Map<String, Long> codeExpirationTimes = new HashMap<>();

    // 生成验证码
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 发送验证码
    public String sendVerificationCode(String phoneNumber) {
        String code = generateCode();
        verificationCodes.put(phoneNumber, code);
        codeExpirationTimes.put(phoneNumber, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(CODE_EXPIRATION_TIME));
        // 模拟发送验证码
        log.info("send to phonenumber {} code is:{} ", phoneNumber, code);
        return code;
    }

    // 验证验证码
    public boolean verifyCode(String phoneNumber, String code) {
        if (verificationCodes.containsKey(phoneNumber)) {
            String storedCode = verificationCodes.get(phoneNumber);
            long expirationTime = codeExpirationTimes.get(phoneNumber);
            if (System.currentTimeMillis() <= expirationTime && storedCode.equals(code)) {
                // 验证成功后移除验证码
                verificationCodes.remove(phoneNumber);
                codeExpirationTimes.remove(phoneNumber);
                return true;
            }
        }
        return false;
    }
}
