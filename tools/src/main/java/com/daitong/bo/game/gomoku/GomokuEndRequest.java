package com.daitong.bo.game.gomoku;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GomokuEndRequest {
    private String roomId;
    private String winnerId;
}
