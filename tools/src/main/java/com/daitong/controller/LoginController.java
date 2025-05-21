package com.daitong.controller;

import com.daitong.bo.common.BaseResponse;
import com.daitong.bo.common.CommonResponse;
import com.daitong.exception.BaseException;
import com.daitong.manager.SessionManager;
import com.daitong.manager.UserManager;
import com.daitong.repository.UserRepository;
import com.daitong.repository.entity.UserEntity;
import com.daitong.utils.ResponseUtils;
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
    public BaseResponse login(@RequestBody Map<String, String> loginData, HttpServletRequest request, HttpServletResponse response) {
        String username = loginData.get("username");
        String password = loginData.get("password");
        BaseResponse baseResponse = new BaseResponse();
        // 验证用户信息
        if (userRepository.checkUser(username, password)) {
            // 生成并设置 Cookie
            String sessionId = SessionManager.generateSessionId();
            Cookie cookie = new Cookie("sessionId", sessionId);
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 设置 Cookie 有效期为 1 小时
            response.addCookie(cookie);
            UserEntity entity = userRepository.getUserInfo(username, password);
            baseResponse.setData(entity);
            // 将会话 ID 添加到会话管理器
            SessionManager.addSession(sessionId, entity);
            response.addHeader("userId", entity.getUserId());
            response.addHeader("Access-Control-Expose-Headers", "userid");
            baseResponse.setCode("200");
            baseResponse.setMessage("ok");
            return baseResponse;
        } else {
            baseResponse.setCode("500");
            baseResponse.setMessage("login fail");
            return baseResponse;
        }
    }

    @GetMapping("/getCouple")
    public BaseResponse login(String coupleId) {
        BaseResponse baseResponse = new BaseResponse();
        try{
            UserEntity entity = userRepository.getCoupleInfo(coupleId, UserManager.getCurrentUser().getUserId());
            baseResponse.setData(entity);
            baseResponse.setCode("200");
            baseResponse.setMessage("ok");
            return baseResponse;
        }catch (BaseException e){
             return  ResponseUtils.baseFailureRes(e);
        }catch (Exception e){
            return  ResponseUtils.baseFailureRes();
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