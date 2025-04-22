package com.daitong.bo.common;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaseResponse extends CommonResponse{
    private Object data;
}
