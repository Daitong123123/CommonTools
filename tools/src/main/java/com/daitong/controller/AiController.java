package com.daitong.controller;

import com.daitong.bo.aichat.ChatRequest;
import com.daitong.bo.aichat.ChatResponse;
import com.daitong.service.QwenChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
public class AiController {

    @Autowired
    private QwenChatService qwenChatService;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
        ChatResponse chatResponse = new ChatResponse();
        try{
            chatResponse.setCode("200");
            chatResponse.setMessage("请求成功");
            chatResponse.setData(qwenChatService.chat(chatRequest.getContent()));
            return chatResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            chatResponse.setCode("500");
            chatResponse.setMessage(e.getMessage());
        }
        return chatResponse;
    }
}
