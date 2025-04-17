package com.daitong.bo.websocket;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class WebMessage {
    private String type;
    private String content;
}
