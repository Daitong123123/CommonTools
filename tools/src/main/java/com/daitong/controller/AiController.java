package com.daitong.controller;

import com.alibaba.fastjson.JSONObject;
import com.daitong.bo.aichat.ChatRequest;
import com.daitong.bo.aichat.ChatResponse;
import com.daitong.bo.aichat.DishRequest;
import com.daitong.bo.aichat.DishResponse;
import com.daitong.bo.aichat.DishResult;
import com.daitong.constants.Promotes;
import com.daitong.service.DoubaoChatService;
import com.daitong.service.QwenChatService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Log4j2
public class AiController {

    @Autowired
    private QwenChatService qwenChatService;

    @Autowired
    private DoubaoChatService doubaoChatService;

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

    @PostMapping("/dish")
    public DishResponse dish(@RequestBody DishRequest dishRequest) {
        List<DishResult> dishList = new ArrayList<>();
        DishResponse dishResponse = new DishResponse();
        DishResult dishResult = new DishResult();
        dishList.add(dishResult);
        String content = String.format(Promotes.DISH_RECOMMEND_USER, dishRequest.getDishType(), dishRequest.getDishNumber(), dishRequest.getDishTaste(),
                JSONObject.toJSONString(dishList));
        try{
            dishResponse.setCode("200");
            dishResponse.setMessage("请求成功");
            String result = qwenChatService.chatByHttp(content, Promotes.DISH_RECOMMEND_SYS);
            log.info("result:"+result);
            dishResponse.setData(JSONObject.parseArray(result, DishResult.class));
            return dishResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            dishResponse.setCode("500");
            dishResponse.setMessage(e.getMessage());
        }
        return dishResponse;
    }


}
