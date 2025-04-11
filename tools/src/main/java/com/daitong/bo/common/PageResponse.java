package com.daitong.bo.common;

import lombok.Data;

@Data
public class PageResponse extends CommonResponse{

    private Integer curPage;

    private Integer pageSize;

    private Integer total;

}
