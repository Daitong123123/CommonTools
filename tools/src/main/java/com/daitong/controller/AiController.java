package com.daitong.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.daitong.bo.aichat.CookBookLikesResponse;
import com.daitong.bo.aichat.*;
import com.daitong.bo.common.BaseResponse;
import com.daitong.bo.common.CommonResponse;
import com.daitong.bo.common.PageRequest;
import com.daitong.constants.Promotes;
import com.daitong.manager.IdManager;
import com.daitong.manager.UserManager;
import com.daitong.repository.CookBookCacheRepository;
import com.daitong.repository.CookBookPreferenceRepository;
import com.daitong.repository.DishDisappearRepository;
import com.daitong.repository.entity.CookBookCache;
import com.daitong.repository.entity.CookBookPreference;
import com.daitong.repository.entity.DishDisappear;
import com.daitong.service.AiChatService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Log4j2
public class AiController {

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private DishDisappearRepository dishDisappearRepository;

    @Autowired
    private CookBookPreferenceRepository cookBookPreferenceRepository;

    @Autowired
    private CookBookCacheRepository cookBookCacheRepository;


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
            List<CookBookPreference> toInsert = unlikeRequest.getUnlikes().stream().filter(unlike->!cookBookPreferenceRepository.isUnLike(unlike)).map(s -> {
                CookBookPreference cookBookPreference = new CookBookPreference();
                cookBookPreference.setId(IdManager.getIdString());
                cookBookPreference.setDishId(s);
                cookBookPreference.setUserId(UserManager.getCurrentUser().getUserId());
                cookBookPreference.setIsLike(false);
                cookBookPreference.setUpdatedAt(now);
                cookBookPreference.setCreatedAt(now);
                return cookBookPreference;
            }).collect(Collectors.toList());
            cookBookPreferenceRepository.saveBatch(toInsert);
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
            List<Long> cancleList = unlikeRequest.getUnlikes().stream().map(Long::parseLong).collect(Collectors.toList());
            cookBookPreferenceRepository.remove(new QueryWrapper<CookBookPreference>().lambda()
                    .eq(CookBookPreference::getIsLike, false)
                    .eq(CookBookPreference::getUserId, UserManager.getCurrentUser().getUserId())
                    .in(CookBookPreference::getDishId, cancleList));
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
            List<String> unLikesId = Optional.ofNullable(cookBookPreferenceRepository.getUnLikesId()).orElse(new ArrayList<>());
            unlikeResponse.setTotal(unLikesId.size());
            unlikeResponse.setCurPage(pageRequest.getCurPage());
            unlikeResponse.setPageSize(pageRequest.getPageSize());
            List<String> dishName = cookBookCacheRepository.selectObjs("dish_name", unLikesId).stream()
                    .map(String::valueOf)
                    .skip((long) (pageRequest.getCurPage() - 1) * pageRequest.getPageSize())
                    .limit(pageRequest.getPageSize())
                    .collect(Collectors.toList());
            unlikeResponse.setUnlikes(dishName);
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
        List<CookBookCache> cookBookCaches = cookBookCacheRepository.findDishInCache(dishRequest.getComplexStart(), dishRequest.getComplexEnd(),
                dishRequest.getPreference(), dishRequest.getDishTaste());
        int cacheDishNumber = 0;
        if(CollectionUtils.isNotEmpty(cookBookCaches)){
            cacheDishNumber = cookBookCaches.size();
        }
        if(cookBookCaches.size() >= dishRequest.getDishNumber()){
            cookBookCaches = selectRandomElements(cookBookCaches, dishRequest.getDishNumber());
        }
        String content = String.format(Promotes.DISH_RECOMMEND_USER, dishRequest.getDishType(), dishRequest.getDishNumber() - cacheDishNumber, dishRequest.getDishTaste(),
                getComplex(dishRequest.getComplexStart(),dishRequest.getComplexEnd()),dishRequest.getPreference(),JSONObject.toJSONString(dishList));
        try{
            dishResponse.setCode("200");
            dishResponse.setMessage("请求成功");
            if(cacheDishNumber < dishRequest.getDishNumber()){
                List<String> cacheNames = cookBookCaches.stream().map(CookBookCache::getDishName).collect(Collectors.toList());
                //屏蔽缓存的菜
                content = setIgnoreDish(cacheNames, content);
                String result = aiChatService.chatToAiByConfig(content, Promotes.DISH_RECOMMEND_SYS);
                List<DishResult> aiResults = JSONObject.parseArray(result, DishResult.class);
                List<CookBookCache> aiData = aiResults.stream().map(re -> {
                    CookBookCache cookBookCache = new CookBookCache();
                    cookBookCache.setComplex(Integer.parseInt(re.getComplex()));
                    cookBookCache.setDishCost(re.getDishCost());
                    cookBookCache.setDishStep(re.getDishStep());
                    cookBookCache.setDishEffect(re.getDishEffect());
                    cookBookCache.setDishFrom(dishRequest.getDishTaste());
                    cookBookCache.setDishName(re.getDishName());
                    cookBookCache.setId(IdManager.getIdString());
                    cookBookCache.setTasty(dishRequest.getPreference());
                    cookBookCache.setDishIngredients(re.getDishIngredients());
                    cookBookCache.setCreatedAt(new Date());
                    cookBookCache.setUpdatedAt(new Date());
                    return cookBookCache;
                }).collect(Collectors.toList());
                //保存ai生成的
                cookBookCacheRepository.saveBatch(aiData);
                log.info("result:"+result);
                //合并缓存列表和生成的列表
                cookBookCaches.addAll(aiData);
            }

            dishResponse.setData(cookBookCaches);
            return dishResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            dishResponse.setCode("500");
            dishResponse.setMessage(e.getMessage());
        }
        return dishResponse;
    }

    public String setIgnoreDish(List<String> cacheNames, String content){
        if(CollectionUtils.isNotEmpty(cacheNames)){
            StringBuilder stringBuilder = new StringBuilder(content);
            stringBuilder.append("请不要推荐下面些菜名的菜");
            cacheNames.forEach(dish-> stringBuilder.append(dish).append(" "));
            stringBuilder.append("。");
            return stringBuilder.toString();
        }
        return content;
    }



    public static List<CookBookCache> selectRandomElements(List<CookBookCache> cookBookCaches, int n) {
        List<CookBookCache> shuffledList = new ArrayList<>(cookBookCaches);
        Collections.shuffle(shuffledList, new Random());
        return shuffledList.subList(0, n);
    }

    private String getComplex(Integer startComplex, Integer endComplex){
        if(startComplex == null && endComplex == null){
            return "1星到10星(包含1星和10星)";
        }
        if(startComplex == null){
            return  endComplex+"星及"+endComplex+"星以下";
        }
        if(endComplex == null){
            return  startComplex+"星及"+startComplex+"星以上";
        }
        if(startComplex.equals(endComplex)){
            return  startComplex+"星";
        }
        return startComplex+"星和"+endComplex+"星之间(包含边界)";
    }

    @PostMapping("/query-likes")
    public CookBookLikesResponse queryLikes(@RequestBody QueryLikesRequest queryLikesRequest){
        CookBookLikesResponse cookBookLikesResponse = new CookBookLikesResponse();
        try{
            cookBookLikesResponse.setCode("200");
            cookBookLikesResponse.setMessage("请求成功");

            List<String> likeIds = cookBookPreferenceRepository.getLikesId();
            List<CookBookCache> cookList = CollectionUtils.isEmpty(likeIds)?Collections.emptyList():cookBookCacheRepository.list(
                    new QueryWrapper<CookBookCache>()
                            .lambda()
                            .in(CookBookCache::getId, likeIds)
                            .eq(StringUtils.isNotEmpty(queryLikesRequest.getDishFrom()), CookBookCache::getDishFrom, queryLikesRequest.getDishFrom())
                            .eq(StringUtils.isNotEmpty(queryLikesRequest.getTasty()), CookBookCache::getTasty, queryLikesRequest.getTasty())
                            .eq(StringUtils.isNotEmpty(queryLikesRequest.getComplex()), CookBookCache::getComplex, queryLikesRequest.getComplex())
            );
            cookBookLikesResponse.setTotal(cookList.size());
            cookBookLikesResponse.setCurPage(queryLikesRequest.getCurPage());
            cookBookLikesResponse.setPageSize(queryLikesRequest.getPageSize());
            List<CookBookCache> cookLikes = cookList.stream().skip((long) (queryLikesRequest.getCurPage() - 1) * queryLikesRequest.getPageSize())
                    .limit(queryLikesRequest.getPageSize())
                    .collect(Collectors.toList());

            cookBookLikesResponse.setCookBookLikesList(cookLikes);
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
            List<String> likesId = cookBookPreferenceRepository.getLikesId();
            List<String> dishFromList = cookBookCacheRepository.selectObjs("dish_from", likesId).stream().map(String::valueOf).collect(Collectors.toList());
            List<String> compelxList = cookBookCacheRepository.selectObjs("complex", likesId).stream().map(String::valueOf).collect(Collectors.toList());
            List<String> tastyList = cookBookCacheRepository.selectObjs("tasty", likesId).stream().map(String::valueOf).collect(Collectors.toList());
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
            CookBookPreference cookBookPreference = new CookBookPreference();
            cookBookPreference.setId(IdManager.getIdString());
            cookBookPreference.setDishId(cookBookLikeRequest.getDishId());
            cookBookPreference.setIsLike(true);
            cookBookPreference.setUserId(UserManager.getCurrentUser().getUserId());
            cookBookPreference.setUpdatedAt(new Date());
            cookBookPreference.setCreatedAt(new Date());
            cookBookPreferenceRepository.save(cookBookPreference);
            return commonResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @PostMapping("/delete-likes")
    public CommonResponse deleteLikes(@RequestBody List<String> deleteList){
        CommonResponse commonResponse = new CommonResponse();
        try{
            commonResponse.setCode("200");
            commonResponse.setMessage("请求成功");
            QueryWrapper<CookBookPreference> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(CookBookPreference::getUserId, UserManager.getCurrentUser().getUserId())
                    .eq(CookBookPreference::getIsLike, true)
                    .in(CookBookPreference::getDishId, deleteList);
            cookBookPreferenceRepository.remove(queryWrapper);
            return commonResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            commonResponse.setCode("500");
            commonResponse.setMessage(e.getMessage());
        }
        return commonResponse;
    }

    @GetMapping("/isLike")
    public BaseResponse isLike(String dishId){
        BaseResponse baseResponse = new BaseResponse();
        try{
            baseResponse.setCode("200");
            baseResponse.setMessage("请求成功");
            baseResponse.setData(cookBookPreferenceRepository.isLike(dishId));
            return baseResponse;
        }catch (Exception e){
            log.error("请求失败", e);
            baseResponse.setCode("500");
            baseResponse.setData(false);
            baseResponse.setMessage(e.getMessage());
        }
        return baseResponse;
    }


}
