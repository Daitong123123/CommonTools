package com.daitong.controller;

import com.daitong.bo.common.BaseResponse;
import com.daitong.bo.translate.TranslateRequest;
import com.daitong.exception.BaseException;
import com.daitong.service.BaiduTranslateService;
import com.daitong.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TranslateController {

    @Autowired
    private BaiduTranslateService baiduTranslateService;


    /**
     * translate
     *
     * @param translateRequest translateRequest
     * @return String
     */
    @PostMapping("/translate")
    public BaseResponse translate(@RequestBody TranslateRequest translateRequest) {
        try {
            BaseResponse res = ResponseUtils.baseSuccessRes();
            String result = baiduTranslateService.translateByBaiduMachine(translateRequest.getContent(),
                    translateRequest.getOriginalLanguage(), translateRequest.getTargetLanguage());
            res.setData(result);
            return res;
        } catch (BaseException baseException) {
            return ResponseUtils.baseFailureRes(baseException);
        } catch (Exception e) {
            return ResponseUtils.baseFailureRes();
        }
    }
}
