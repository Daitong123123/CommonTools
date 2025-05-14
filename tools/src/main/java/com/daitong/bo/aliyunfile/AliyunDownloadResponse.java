package com.daitong.bo.aliyunfile;

import com.daitong.bo.common.CommonResponse;
import lombok.Data;

@Data
public class AliyunDownloadResponse extends CommonResponse {

    private String type;

    private String data;

    private String dataUrl;

}
