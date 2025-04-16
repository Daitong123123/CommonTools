package com.daitong.service;

import com.daitong.bo.message.SendMessageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendMessage(SendMessageRequest messageRequest) {
        // 处理消息发送逻辑...
        // 假设消息发送成功
        // 向B发送通知，通知其刷新聊天记录
        simpMessagingTemplate.convertAndSend("/topic/" + messageRequest.getUserIdTo(), "有新消息，请刷新聊天记录");
    }
}
