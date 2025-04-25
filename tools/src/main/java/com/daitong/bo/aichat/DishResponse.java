package com.daitong.bo.aichat;

import com.daitong.bo.common.CommonResponse;
import com.daitong.repository.entity.CookBookCache;
import lombok.Data;

import java.util.List;

@Data
public class DishResponse extends CommonResponse {

    private List<CookBookCache> data;
}
