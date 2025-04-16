package com.daitong.bo.message;

import com.daitong.bo.common.CommonResponse;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FriendInfoResponse extends CommonResponse {
    private String userId;
    private String userNickName;
}
