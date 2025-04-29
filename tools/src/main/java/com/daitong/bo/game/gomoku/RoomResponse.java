package com.daitong.bo.game.gomoku;

import com.daitong.bo.common.CommonResponse;
import com.daitong.bo.common.UserInfo;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class RoomResponse extends CommonResponse {

    private String gameStatus;
    private List<String> userInfos;
    private Room room;
}
