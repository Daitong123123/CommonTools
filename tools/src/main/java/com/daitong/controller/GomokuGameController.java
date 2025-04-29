package com.daitong.controller;

import com.alibaba.fastjson2.JSONObject;
import com.daitong.bo.common.BaseResponse;
import com.daitong.bo.game.gomoku.*;
import com.daitong.bo.message.SendMessageRequest;
import com.daitong.repository.ChatRecordRepository;
import com.daitong.service.ChatService;
import com.daitong.service.GomokuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/gomoku")
public class GomokuGameController {

    @Autowired
    private GomokuService gomokuService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRecordRepository chatRecordRepository;

    @PostMapping("/room/create")
    public CreateResponse createRoom(@RequestParam String userId) {
        String invitationCode = gomokuService.createRoom(userId);
        CreateResponse response = new CreateResponse();
        response.setInviteCode(invitationCode);
        // 这里简单假设 roomId 可以通过 invitationCode 再次查找得到
        // 在实际中应该直接从创建方法返回 roomId
        for (Room room : gomokuService.getRooms().values()) {
            if (room.getInvitationCode().equals(invitationCode)) {
                response.setRoomId(room.getRoomId());
                break;
            }
        }
        return response;
    }

    @PostMapping("/room/join")
    public JoinResponse joinRoom(@RequestParam String userId, @RequestParam String invitationCode) {
        String roomId = gomokuService.joinRoom(userId, invitationCode);
        JoinResponse response = new JoinResponse();
        if (roomId != null) {
            response.setRoomId(roomId);
            response.setSuccess(true);
            response.setPlayers(gomokuService.getRoomStatus(roomId).getPlayerIds());
        } else {
            response.setSuccess(false);
        }
        return response;
    }

    @GetMapping("/invite")
    public CreateResponse inviteGame(@RequestParam String userId, @RequestParam String friendId) {
        CreateResponse createResponse = createRoom(userId);
        SendMessageRequest sendMessageRequest = new SendMessageRequest();
        sendMessageRequest.setUserIdFrom(userId);
        sendMessageRequest.setUserIdTo(friendId);
        sendMessageRequest.setMessageType("Gomoku");
        GomokuInfo gomokuInfo = new GomokuInfo();
        gomokuInfo.setInviteCode(createResponse.getInviteCode());
        gomokuInfo.setRoomId(createResponse.getRoomId());
        sendMessageRequest.setMessageContent(JSONObject.toJSONString(gomokuInfo));
        chatRecordRepository.insertRecord(sendMessageRequest);
        chatService.sendMessage(sendMessageRequest);
        return createResponse;
    }

    @GetMapping("/room/status")
    public RoomResponse getRoomStatus(@RequestParam String roomId) {
        RoomResponse response = new RoomResponse();
        response.setRoom(gomokuService.getRoomStatus(roomId));
        return response;
    }

    @PostMapping("/game/start")
    public GomokuStartResponse startGame(@RequestParam String roomId) {
        boolean result = gomokuService.startGame(roomId);
        GomokuStartResponse response = new GomokuStartResponse();
        response.setFirstId("");
        response.setSuccess(result);
        return response;
    }

    @PostMapping("/game/move")
    public GomokuMoveResponse makeMove(@RequestParam String roomId, @RequestParam String userId,
                                         @RequestParam int x, @RequestParam int y) {
        boolean result = gomokuService.makeMove(roomId, userId, x, y);
        GomokuMoveResponse response = new GomokuMoveResponse();
        response.setMoveSuccess( result);
        Room room = gomokuService.getRoomStatus(roomId);
        if (room.isHasWinner()) {
            response.setHasWinner( true);
            response.setWinnerId( room.getWinnerId());
        } else {
            response.setHasWinner( false);
        }
        return response;
    }

    @PostMapping("/game/end")
    public GomokuEndResponse endGame(@RequestParam String roomId, @RequestParam(required = false) String winnerId) {
        boolean result = gomokuService.endGame(roomId, winnerId);
        GomokuEndResponse response = new GomokuEndResponse();
        response.setEnded(result);
        response.setWinnerId(winnerId);
        return response;
    }
}