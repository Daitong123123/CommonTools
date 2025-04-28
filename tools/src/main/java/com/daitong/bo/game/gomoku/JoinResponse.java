package com.daitong.bo.game.gomoku;

import com.daitong.bo.common.CommonResponse;
import com.daitong.bo.common.UserInfo;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class JoinResponse extends CommonResponse {
    private String roomId;
    private boolean success;
    private List<String> players;
}
