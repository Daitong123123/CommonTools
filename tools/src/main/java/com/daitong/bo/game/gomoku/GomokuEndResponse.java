package com.daitong.bo.game.gomoku;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GomokuEndResponse {
    private String winnerId;
    private boolean ended;
}
