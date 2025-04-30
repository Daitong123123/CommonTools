package com.daitong.service;

import com.daitong.bo.message.FriendToBeRequest;
import com.daitong.bo.message.SendMessageRequest;
import com.daitong.bo.websocket.GomokuMessage;
import com.daitong.bo.websocket.WebMessage;
import com.daitong.manager.UserManager;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendMessage(SendMessageRequest messageRequest) {
        WebMessage webMessage = new WebMessage();
        webMessage.setType("newMessage");
        webMessage.setSenderId(messageRequest.getUserIdFrom());
        webMessage.setContent(messageRequest.getMessageContent());
        simpMessagingTemplate.convertAndSend("/topic/" + messageRequest.getUserIdTo(), webMessage);
        simpMessagingTemplate.convertAndSend("/topic/" + messageRequest.getUserIdFrom(), webMessage);
    }

    public void onGomokuJoin(String roomId) {
        GomokuMessage message = new GomokuMessage();
        message.setMessageType("onJoin");
        simpMessagingTemplate.convertAndSend("/topic/gomoku/" + roomId, message);
    }

    public void onGomokuLeave(String roomId) {
        GomokuMessage message = new GomokuMessage();
        message.setMessageType("onLeave");
        message.setUserId(UserManager.getCurrentUser().getUserId());
        simpMessagingTemplate.convertAndSend("/topic/gomoku/" + roomId, message);
    }

    public void onGomokuStart(String roomId, String firstId) {
        GomokuMessage message = new GomokuMessage();
        message.setMessageType("onStart");
        message.setUserId(firstId);
        simpMessagingTemplate.convertAndSend("/topic/gomoku/" + roomId, message);
    }

    public void onGomokuMove(String roomId, String nextUserId, boolean hasWinner, String winnerId) {
        GomokuMessage message = new GomokuMessage();
        message.setMessageType("onMove");
        message.setUserId(nextUserId);message.setHasWinner(hasWinner);
        message.setWinnerId(winnerId);
        simpMessagingTemplate.convertAndSend("/topic/gomoku/" + roomId, message);
    }

    public void sendReadMessage(SendMessageRequest messageRequest) {
        WebMessage webMessage = new WebMessage();
        webMessage.setType("readMessage");
        webMessage.setContent(messageRequest.getMessageContent());
        webMessage.setSenderId(messageRequest.getUserIdFrom());
        simpMessagingTemplate.convertAndSend("/topic/" + messageRequest.getUserIdTo(), webMessage);
        simpMessagingTemplate.convertAndSend("/topic/" + messageRequest.getUserIdFrom(), webMessage);
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
