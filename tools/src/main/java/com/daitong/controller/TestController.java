package com.daitong.controller;

import com.daitong.bo.translate.TranslateRequest;
import com.daitong.service.BaiduTranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private BaiduTranslateService baiduTranslateService;

    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }

    @PostMapping("/translate")
    public String translate(@RequestBody TranslateRequest translateRequest){
        return baiduTranslateService.translateByBaiduMachine(translateRequest.getContent(), translateRequest.getOriginalLanguage(), translateRequest.getTargetLanguage());
    }
}
