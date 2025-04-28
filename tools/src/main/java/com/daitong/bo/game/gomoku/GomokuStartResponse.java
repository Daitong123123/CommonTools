package com.daitong.bo.game.gomoku;

import com.daitong.bo.common.UserInfo;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class GomokuStartResponse {
    private String firstId;
    private boolean success;
}
