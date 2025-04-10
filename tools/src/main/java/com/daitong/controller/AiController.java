package com.daitong.controller;

import com.alibaba.fastjson.JSONObject;
import com.daitong.bo.aichat.ChatRequest;
import com.daitong.bo.aichat.ChatResponse;
import com.daitong.bo.aichat.DishRequest;
import com.daitong.bo.aichat.DishResponse;
import com.daitong.bo.aichat.DishResult;
import com.daitong.constants.Promotes;
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
//            String result = qwenChatService.chat(content, Promotes.DISH_RECOMMEND_SYS);
            String result = "\"[{\\\"complex\\\":2,\\\"dishName\\\":\\\"麻婆豆腐\\\",\\\"dishStep\\\":\\\"准备食材：嫩豆腐400克，牛肉末50克，青蒜苗2根，郫县豆瓣酱20克，生抽10克，料酒10克，淀粉5克，花椒粉2克，辣椒粉3克，盐2克，糖1克，鸡精1克，水200毫升，食用油20克。\\\\n1. 豆腐切块，放入加了盐的沸水中焯水1分钟，捞出备用\\\\n2. 青蒜苗切段，郫县豆瓣酱剁细\\\\n3. 热锅冷油，加入牛肉末炒至变色，约需2分钟\\\\n4. 加入郫县豆瓣酱、辣椒粉，小火炒香1分钟\\\\n5. 倒入水、生抽、料酒、糖和盐，煮开后加入豆腐，中小火煮5分钟\\\\n6. 淀粉加水调匀，倒入锅中勾芡，翻拌均匀\\\\n7. 加入青蒜苗，再煮1分钟\\\\n8. 最后撒上花椒粉、鸡精，翻拌均匀即可出锅\\\"}]\"";
            result = result.replace("\\n","&NBSP").replace("\\","").replace("&NBSP","\\n");
            result = result.substring(1,result.length()-1);
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
