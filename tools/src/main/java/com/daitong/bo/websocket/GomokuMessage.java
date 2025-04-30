package com.daitong.bo.websocket;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GomokuMessage {

    private boolean hasWinner;
    private String winnerId;
    private String messageType;
    private String userId;
    private String content;
}
