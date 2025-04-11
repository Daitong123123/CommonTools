package com.daitong.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.daitong.bo.aichat.ChatRequest;
import com.daitong.bo.aichat.ChatResponse;
import com.daitong.bo.aichat.DishRequest;
import com.daitong.bo.aichat.DishResponse;
import com.daitong.bo.aichat.DishResult;
import com.daitong.bo.aichat.UnlikeRequest;
import com.daitong.bo.aichat.UnlikeResponse;
import com.daitong.bo.common.CommonResponse;
import com.daitong.bo.common.PageRequest;
import com.daitong.constants.Promotes;
import com.daitong.repository.DishDisappearRepository;
import com.daitong.repository.entity.DishDisappear;
import com.daitong.service.AiChatService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class AiController {

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private DishDisappearRepository dishDisappearRepository;


    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest chatRequest) {
        ChatResponse chatResponse = new ChatResponse();
        try{
            chatResponse.setCode("200");
            chatResponse.setMessage("请求成功");
            chatResponse.setData(aiChatService.chat(chatRequest.getContent()));
            return chatResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            chatResponse.setCode("500");
            chatResponse.setMessage(e.getMessage());
        }
        return chatResponse;
    }

    @PostMapping("/unlike")
    public CommonResponse unlike(@RequestBody UnlikeRequest unlikeRequest) {
        CommonResponse commonResponse = new CommonResponse();
        try{
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
            Date now = new Date();
            unlikeRequest.getUnlikes().forEach(s->{
                DishDisappear dishDisappear = new DishDisappear();
                dishDisappear.setDishName(s);
                dishDisappear.setCreatedAt(now);
                dishDisappearRepository.save(dishDisappear);
            });
            return commonResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @PostMapping("/unlike-cancel")
    public CommonResponse unlikeCancel(@RequestBody UnlikeRequest unlikeRequest) {
        CommonResponse commonResponse = new CommonResponse();
        try{
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
            unlikeRequest.getUnlikes().forEach(s->{
                QueryWrapper<DishDisappear> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(DishDisappear::getDishName, s);
                dishDisappearRepository.remove(queryWrapper);
            });
            return commonResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @PostMapping("/unlike-list")
    public UnlikeResponse unlikeList(@RequestBody PageRequest pageRequest) {
        UnlikeResponse unlikeResponse = new UnlikeResponse();
        try{
            unlikeResponse.setCode("200");
            unlikeResponse.setMessage("请求成功");
            List<DishDisappear> list = dishDisappearRepository.list();
            unlikeResponse.setTotal(CollectionUtils.isEmpty(list)?0:list.size());
            unlikeResponse.setCurPage(pageRequest.getCurPage());
            unlikeResponse.setPageSize(pageRequest.getPageSize());
            if(CollectionUtils.isEmpty(list)){
                unlikeResponse.setUnlikes(new ArrayList<>());
            }else {
                List<String> unlikes = list.stream()
                        .skip((long) (pageRequest.getCurPage() - 1) * pageRequest.getPageSize())
                        .limit(pageRequest.getPageSize())
                        .map(DishDisappear::getDishName)
                        .collect(Collectors.toList());
                unlikeResponse.setUnlikes(unlikes);
            }
            return unlikeResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            unlikeResponse.setCode("500");
            unlikeResponse.setMessage(e.getMessage());
        }
        return unlikeResponse;
    }

    @PostMapping("/dish")
    public DishResponse dish(@RequestBody DishRequest dishRequest) {
        List<DishResult> dishList = new ArrayList<>();
        DishResponse dishResponse = new DishResponse();
        DishResult dishResult = new DishResult();
        dishList.add(dishResult);
        String content = String.format(Promotes.DISH_RECOMMEND_USER, dishRequest.getDishType(), dishRequest.getDishNumber(), dishRequest.getDishTaste(),
                dishRequest.getComplex(),dishRequest.getPreference(),JSONObject.toJSONString(dishList));
        try{
            dishResponse.setCode("200");
            dishResponse.setMessage("请求成功");
            String result = aiChatService.chatToDoubao(content, Promotes.DISH_RECOMMEND_SYS);
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
