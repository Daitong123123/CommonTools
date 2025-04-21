package com.daitong.bo.aichat;

import com.daitong.bo.common.CommonResponse;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CookBookLikesOptionsResponse extends CommonResponse {
    private List<String> dishFromList;
    private List<String> compelxList;
    private List<String> tastyList;
}
