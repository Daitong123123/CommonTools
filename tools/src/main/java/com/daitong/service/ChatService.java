package com.daitong.service;

import com.daitong.bo.message.FriendToBeRequest;
import com.daitong.bo.message.SendMessageRequest;
import com.daitong.bo.websocket.WebMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendMessage(SendMessageRequest messageRequest) {
        WebMessage webMessage = new WebMessage();
        webMessage.setType("newMessage");
        webMessage.setContent(messageRequest.getMessageContent());
        simpMessagingTemplate.convertAndSend("/topic/" + messageRequest.getUserIdTo(), webMessage);
    }

    public void sendFriendRequest(FriendToBeRequest friendToBeRequest) {
        WebMessage webMessage = new WebMessage();
        webMessage.setType("friendRequest");
        webMessage.setContent(friendToBeRequest.getContent());
        //刷新自己的申请列表和好友的申请列表
        simpMessagingTemplate.convertAndSend("/topic/" + friendToBeRequest.getFriendId(), webMessage);
        simpMessagingTemplate.convertAndSend("/topic/" + friendToBeRequest.getUserId(), webMessage);
    }

    public void sendFriendRequestAgree(FriendToBeRequest friendToBeRequest) {
        WebMessage webMessage = new WebMessage();
        webMessage.setType("friendRequestAgree");
        webMessage.setContent(friendToBeRequest.getContent());
        //刷新自己的申请列表和好友的申请列表
        simpMessagingTemplate.convertAndSend("/topic/" + friendToBeRequest.getFriendId(), webMessage);
        simpMessagingTemplate.convertAndSend("/topic/" + friendToBeRequest.getUserId(), webMessage);
    }

    public void sendFriendRequestDisagree(FriendToBeRequest friendToBeRequest) {
        WebMessage webMessage = new WebMessage();
        webMessage.setType("friendRequestDisagree");
        webMessage.setContent(friendToBeRequest.getContent());
        //刷新自己的申请列表和好友的申请列表
        simpMessagingTemplate.convertAndSend("/topic/" + friendToBeRequest.getFriendId(), webMessage);
        simpMessagingTemplate.convertAndSend("/topic/" + friendToBeRequest.getUserId(), webMessage);
    }
}
