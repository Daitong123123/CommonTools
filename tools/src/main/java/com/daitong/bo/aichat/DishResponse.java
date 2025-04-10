package com.daitong.bo.aichat;

import com.daitong.bo.common.CommonResponse;
import lombok.Data;

import java.util.List;

@Data
public class DishResponse extends CommonResponse {

    private List<DishResult> data;
}
