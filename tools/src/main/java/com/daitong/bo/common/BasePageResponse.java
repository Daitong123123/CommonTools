package com.daitong.bo.common;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BasePageResponse extends PageResponse{
    private Object data;
}
