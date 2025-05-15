package com.daitong.utils;

import com.daitong.bo.common.BaseResponse;
import com.daitong.exception.BaseException;

public class ResponseUtils {

    public static BaseResponse baseSuccessRes(){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode("200");
        baseResponse.setMessage("ok");
        return baseResponse;
    }

    public static BaseResponse baseFailureRes(){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode("500");
        baseResponse.setMessage("fail");
        return baseResponse;
    }

    public static BaseResponse baseFailureRes(BaseException baseException){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(baseException.getCode());
        baseResponse.setMessage(baseResponse.getMessage());
        return baseResponse;
    }

}
