package com.daitong.controller;

import com.daitong.bo.common.CommonResponse;
import com.daitong.manager.SessionManager;
import com.daitong.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public CommonResponse login(@RequestBody Map<String, String> loginData, HttpServletRequest request, HttpServletResponse response) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        CommonResponse commonResponse = new CommonResponse();
        // 验证用户信息
        if (userRepository.checkUser(username, password)) {
            // 生成并设置 Cookie
            String sessionId = SessionManager.generateSessionId();
            Cookie cookie = new Cookie("sessionId", sessionId);
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 设置 Cookie 有效期为 1 小时
            response.addCookie(cookie);
            // 将会话 ID 添加到会话管理器
            SessionManager.addSession(sessionId, username, userRepository.getId(username, password));
            commonResponse.setCode("200");
            commonResponse.setMessage("ok");
            return commonResponse;
        } else {
            commonResponse.setCode("500");
            commonResponse.setMessage("login fail");
            return commonResponse;
        }
    }

    @PostMapping("/register")
    public CommonResponse register(@RequestBody Map<String, String> registerData) {
        String username = registerData.get("username");
        String password = registerData.get("password");
        CommonResponse commonResponse = new CommonResponse();

        // 检查用户名是否已存在
        if (userRepository.isUsernameExists(username)) {
            commonResponse.setCode("500");
            commonResponse.setMessage("用户名已存在");
            return commonResponse;
        }


        //   注册新用户
        if (userRepository.registerUser(username, password)) {
            commonResponse.setCode("200");
            commonResponse.setMessage("注册成功");
        } else {
            commonResponse.setCode("500");
            commonResponse.setMessage("注册失败");
        }
        return commonResponse;
    }
}