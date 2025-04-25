package com.daitong.bo.aichat;

import com.daitong.bo.common.PageResponse;
import com.daitong.repository.entity.CookBookCache;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class CookBookLikesResponse extends PageResponse {

    private List<CookBookCache> cookBookLikesList;
}
