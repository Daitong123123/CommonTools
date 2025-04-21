package com.daitong.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daitong.bo.aichat.CookBookLikesResponse;
import com.daitong.bo.aichat.*;
import com.daitong.bo.common.CommonResponse;
import com.daitong.bo.common.PageRequest;
import com.daitong.constants.Promotes;
import com.daitong.manager.UserManager;
import com.daitong.repository.CookBookLikesRepository;
import com.daitong.repository.DishDisappearRepository;
import com.daitong.repository.entity.CookBookLikes;
import com.daitong.repository.entity.DishDisappear;
import com.daitong.service.AiChatService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    private CookBookLikesRepository cookBookLikesRepository;


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
                dishDisappear.setUserId(UserManager.getCurrentUser().getUserId());
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
                queryWrapper.lambda().eq(DishDisappear::getDishName, s)
                        .eq(DishDisappear::getUserId, UserManager.getCurrentUser().getUserId());
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
           List<DishDisappear> list = dishDisappearRepository.getUnlikes();
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

    @PostMapping("/query-likes")
    public CookBookLikesResponse queryLikes(@RequestBody QueryLikesRequest queryLikesRequest){
        CookBookLikesResponse cookBookLikesResponse = new CookBookLikesResponse();
        try{
            cookBookLikesResponse.setCode("200");
            cookBookLikesResponse.setMessage("请求成功");
            Page<CookBookLikes> page = new Page<>(queryLikesRequest.getCurPage(), queryLikesRequest.getPageSize());
            IPage<CookBookLikes> list = cookBookLikesRepository.page(page,new QueryWrapper<CookBookLikes>()
                    .lambda().eq(CookBookLikes::getUserId, UserManager.getCurrentUser().getUserId())
                    .eq(StringUtils.isNotEmpty(queryLikesRequest.getDishFrom()), CookBookLikes::getDishFrom, queryLikesRequest.getDishFrom())
                    .eq(StringUtils.isNotEmpty(queryLikesRequest.getComplex()), CookBookLikes::getComplex, queryLikesRequest.getComplex())
                    .eq(StringUtils.isNotEmpty(queryLikesRequest.getTasty()), CookBookLikes::getTasty, queryLikesRequest.getTasty()));
            cookBookLikesResponse.setTotal((int) list.getTotal());
            cookBookLikesResponse.setCurPage(queryLikesRequest.getCurPage());
            cookBookLikesResponse.setPageSize(queryLikesRequest.getPageSize());
            cookBookLikesResponse.setCookBookLikesList(list.getRecords());
            return cookBookLikesResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            cookBookLikesResponse.setCode("500");
            cookBookLikesResponse.setMessage(e.getMessage());
        }
        return cookBookLikesResponse;
    }

    @GetMapping("/query-likes-options")
    public CookBookLikesOptionsResponse queryLikesOptions(){
        CookBookLikesOptionsResponse cookBookLikesResponse = new CookBookLikesOptionsResponse();
        try{
            cookBookLikesResponse.setCode("200");
            cookBookLikesResponse.setMessage("请求成功");

            List<String> dishFromList = cookBookLikesRepository.selectObjs("dish_from").stream().map(String::valueOf).collect(Collectors.toList());
            List<String> compelxList = cookBookLikesRepository.selectObjs("complex").stream().map(String::valueOf).collect(Collectors.toList());
            List<String> tastyList = cookBookLikesRepository.selectObjs("tasty").stream().map(String::valueOf).collect(Collectors.toList());
            cookBookLikesResponse.setCompelxList(compelxList);
            cookBookLikesResponse.setTastyList(tastyList);
            cookBookLikesResponse.setDishFromList(dishFromList);
            return cookBookLikesResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            cookBookLikesResponse.setCode("500");
            cookBookLikesResponse.setMessage(e.getMessage());
        }
        return cookBookLikesResponse;
    }

    @PostMapping("/add-like")
    public CommonResponse addLike(@RequestBody CookBookLikeRequest cookBookLikeRequest){
        CommonResponse commonResponse = new CommonResponse();
        try{
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
            cookBookLikeRequest.getCookBook().setUserId(UserManager.getCurrentUser().getUserId());
            cookBookLikeRequest.getCookBook().setCreatedAt(new Date());
            cookBookLikeRequest.getCookBook().setUpdatedAt(new Date());
            cookBookLikesRepository.save(cookBookLikeRequest.getCookBook());
            return commonResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @PostMapping("/delete-likes")
    public CommonResponse deleteLikes(@RequestBody List<Long> deleteList){
        CommonResponse commonResponse = new CommonResponse();
        try{
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
            cookBookLikesRepository.removeByIds(deleteList);
            return commonResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }


}
