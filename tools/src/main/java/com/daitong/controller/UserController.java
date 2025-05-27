package com.daitong.controller;

import com.daitong.bo.common.BaseResponse;
import com.daitong.manager.UserManager;
import com.daitong.repository.UserRepository;
import com.daitong.repository.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/update-user-iconId")
    public BaseResponse updateUserInfo(String iconId) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            baseResponse.setData(userRepository.updateIconId(UserManager.getCurrentUser().getUserId(), iconId));
            baseResponse.setCode("200");
            baseResponse.setMessage("上传成功");
            return baseResponse;
        } catch (Exception e) {
            baseResponse.setCode("500");
            return baseResponse;
        }
    }

    @GetMapping("/userinfo")
    public BaseResponse getUserInfo(String userId) {
        BaseResponse baseResponse = new BaseResponse();
        try {
            UserEntity userInfo = userRepository.getUserInfo(userId);
            baseResponse.setData(userInfo);
            baseResponse.setCode("200");
            baseResponse.setMessage("上传成功");
            return baseResponse;
        } catch (Exception e) {
            baseResponse.setCode("500");
            return baseResponse;
        }
    }
}
