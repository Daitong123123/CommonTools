package com.daitong.bo.game.gomoku;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GomokuMoveRequest {

    private String roomId;
    private String userId;
    private Integer x;
    private Integer y;
}
