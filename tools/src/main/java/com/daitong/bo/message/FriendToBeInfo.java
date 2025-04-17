package com.daitong.bo.message;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FriendToBeInfo {

    private String requestFrom;

    private String requestTo;

    private String content;

    private String status;
}
