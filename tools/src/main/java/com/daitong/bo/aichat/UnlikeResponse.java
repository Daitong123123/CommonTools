package com.daitong.bo.aichat;

import com.daitong.bo.common.PageResponse;
import lombok.Data;

import java.util.List;

@Data
public class UnlikeResponse extends PageResponse {
    List<String> unlikes;
}
