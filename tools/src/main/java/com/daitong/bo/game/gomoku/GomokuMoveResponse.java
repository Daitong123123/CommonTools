package com.daitong.bo.game.gomoku;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GomokuMoveResponse {
    private boolean moveSuccess;
    private boolean hasWinner;
    private String winnerId;
}
