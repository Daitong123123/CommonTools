package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.manager.UserManager;
import com.daitong.repository.entity.CookBookPreference;
import com.daitong.repository.mapper.CookBookPreferenceMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CookBookPreferenceRepository extends ServiceImpl<CookBookPreferenceMapper, CookBookPreference> {

    public List<String> getLikesId(){
        List<CookBookPreference> likeList = list(
                new QueryWrapper<CookBookPreference>()
                        .lambda()
                        .eq(CookBookPreference::getIsLike, true)
                        .eq(CookBookPreference::getUserId, UserManager.getCurrentUser().getUserId())
        );
        return Optional.ofNullable(likeList).orElse(new ArrayList<>()).stream().map(CookBookPreference::getDishId).collect(Collectors.toList());
    }

    public List<String> getUnLikesId(){
        List<CookBookPreference> likeList = list(
                new QueryWrapper<CookBookPreference>()
                        .lambda()
                        .eq(CookBookPreference::getIsLike, false)
                        .eq(CookBookPreference::getUserId, UserManager.getCurrentUser().getUserId())
        );
        return Optional.ofNullable(likeList).orElse(new ArrayList<>()).stream().map(CookBookPreference::getDishId).collect(Collectors.toList());
    }


    public boolean isLike(String dishId){
        QueryWrapper<CookBookPreference> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(CookBookPreference::getUserId, UserManager.getCurrentUser().getUserId())
                .eq(CookBookPreference::getIsLike, true)
                .in(CookBookPreference::getDishId, dishId);
        List<CookBookPreference> list = list(queryWrapper);
        return CollectionUtils.isNotEmpty(list);
    }

    public boolean isUnLike(String dishId){
        QueryWrapper<CookBookPreference> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(CookBookPreference::getUserId, UserManager.getCurrentUser().getUserId())
                .eq(CookBookPreference::getIsLike, false)
                .in(CookBookPreference::getDishId, dishId);
        List<CookBookPreference> list = list(queryWrapper);
        return CollectionUtils.isNotEmpty(list);
    }




}
