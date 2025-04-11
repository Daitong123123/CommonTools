package com.daitong.controller;

import com.daitong.bo.common.CommonResponse;
import com.daitong.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/verification")
public class VerificationCodeController {

    @Autowired
    private VerificationCodeService verificationCodeService;

    // 发送验证码接口
    @PostMapping("/send")
    public CommonResponse sendCode(@RequestBody String phoneNumber) {
        CommonResponse response = new CommonResponse();
        try {
            String code = verificationCodeService.sendVerificationCode(phoneNumber);
            response.setCode("200");
            response.setMessage("验证码发送成功");
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("验证码发送失败: " + e.getMessage());
        }
        return response;
    }

    // 验证验证码接口
    @PostMapping("/verify")
    public CommonResponse verifyCode(@RequestBody Map<String, String> request) {
        CommonResponse response = new CommonResponse();
        String phoneNumber = request.get("phoneNumber");
        String code = request.get("code");
        if (verificationCodeService.verifyCode(phoneNumber, code)) {
            response.setCode("200");
            response.setMessage("验证码验证成功");
        } else {
            response.setCode("500");
            response.setMessage("验证码验证失败");
        }
        return response;
    }
}
