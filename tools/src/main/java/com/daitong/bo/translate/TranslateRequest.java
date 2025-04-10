package com.daitong.bo.translate;

import lombok.Data;

@Data
public class TranslateRequest {

    private String content;

    private String originalLanguage = "zh";

    private String targetLanguage = "en";

}
