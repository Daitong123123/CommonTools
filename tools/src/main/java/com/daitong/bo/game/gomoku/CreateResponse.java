package com.daitong.bo.game.gomoku;

import com.daitong.bo.common.CommonResponse;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateResponse extends CommonResponse {

    private String inviteCode;
    private String roomId;
}
