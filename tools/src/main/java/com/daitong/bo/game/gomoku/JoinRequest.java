package com.daitong.bo.game.gomoku;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class JoinRequest {
    private String userId;
    private String inviteCode;
}
