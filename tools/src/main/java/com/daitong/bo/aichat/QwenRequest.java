package com.daitong.bo.aichat;

import lombok.Builder;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
public class QwenRequest {

    private List<Message> messages;

    private Double temperature;

    private Boolean stream;

    private String model;

    private String user;

    @ToString
    @Builder
    public static class Message {
        private String role;

        private String content;
    }

}
