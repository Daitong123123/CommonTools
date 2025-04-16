package com.daitong.bo.message;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SendMessageRequest {

    private String userIdFrom;

    private String userIdTo;

    private String messageType;

    private String messageContent;

}
