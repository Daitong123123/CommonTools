package com.daitong.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.daitong.bo.aichat.ChatResponse;
import com.daitong.bo.common.CommonResponse;
import com.daitong.bo.message.FriendInfoResponse;
import com.daitong.bo.message.FriendShipRequest;
import com.daitong.bo.message.FriendShipResponse;
import com.daitong.bo.message.GetMessageRequest;
import com.daitong.bo.message.MessageResponse;
import com.daitong.bo.message.SendMessageRequest;
import com.daitong.repository.ChatRecordRepository;
import com.daitong.repository.FriendShipRepository;
import com.daitong.repository.UserRepository;
import com.daitong.repository.entity.ChatRecord;
import com.daitong.repository.entity.FriendShip;
import com.daitong.repository.entity.UserEntity;
import com.daitong.service.ChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class ChatController {

    @Autowired
    private ChatRecordRepository chatRecordRepository;

    @Autowired
    private FriendShipRepository friendShipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatService chatService;

    @PostMapping("/send-message")
    public CommonResponse sendMessage(@RequestBody SendMessageRequest messageRequest) {
        CommonResponse commonResponse = new CommonResponse();
        try{
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
           chatRecordRepository.insertRecord(messageRequest);
           chatService.sendMessage(messageRequest);
            return commonResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @PostMapping("/message-query")
    public MessageResponse getMessageHistory(@RequestBody GetMessageRequest getMessageRequest){
        QueryWrapper<ChatRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(ChatRecord::getUserIdFrom, getMessageRequest.getUserIdFrom())
                .eq(ChatRecord::getUserIdTo, getMessageRequest.getUserIdTo());
        List<ChatRecord> records = chatRecordRepository.list(queryWrapper);
        QueryWrapper<ChatRecord> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.lambda()
                .eq(ChatRecord::getUserIdFrom, getMessageRequest.getUserIdTo())
                .eq(ChatRecord::getUserIdTo, getMessageRequest.getUserIdFrom());
        List<ChatRecord> records2 = chatRecordRepository.list(queryWrapper2);
        records.addAll(records2);
        records.sort(Comparator.comparing(ChatRecord::getCreatedAt));
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setTotal(records.size());
        messageResponse.setRecords(records.stream()
                .skip((long) (getMessageRequest.getCurPage() - 1) *getMessageRequest.getPageSize())
                .limit(getMessageRequest.getPageSize()).collect(Collectors.toList()));
        messageResponse.setCurPage(getMessageRequest.getCurPage());
        messageResponse.setPageSize(getMessageRequest.getPageSize());
        return messageResponse;
    }

    @PostMapping("/friend-ship")
    public FriendShipResponse getMyFriends(@RequestBody FriendShipRequest friendShipRequest){
        QueryWrapper<FriendShip> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(FriendShip::getUserId, friendShipRequest.getUserId())
                .eq(FriendShip::getStatus, 1);
        List<FriendShip> friendShips = friendShipRepository.list(queryWrapper);
        FriendShipResponse friendShipResponse = new FriendShipResponse();
        friendShipResponse.setFriends(friendShips.stream().map(FriendShip::getFriendId).collect(Collectors.toList()));
        return friendShipResponse;
    }

    @GetMapping("/friend-info")
    public FriendInfoResponse getFriendInfo(String userId){
        FriendInfoResponse friendInfoResponse = new FriendInfoResponse();
        UserEntity userInfo = userRepository.getUserInfo(userId);
        friendInfoResponse.setUserNickName(userInfo.getUserName());
        friendInfoResponse.setUserId(userId);
        return friendInfoResponse;
    }


}
