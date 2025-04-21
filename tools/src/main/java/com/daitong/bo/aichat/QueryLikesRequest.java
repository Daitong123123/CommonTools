package com.daitong.bo.aichat;

import com.daitong.bo.common.PageRequest;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueryLikesRequest extends PageRequest {
    private String dishFrom;
    private String tasty;
    private String complex;
}
