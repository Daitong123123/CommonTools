package com.daitong.bo.message;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FriendToBeRequest {

    private String userId;

    private String friendId;

    private String content;
}
