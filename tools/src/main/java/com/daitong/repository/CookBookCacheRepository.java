package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.CookBookCache;
import com.daitong.repository.mapper.CookBookCacheMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static javax.management.Query.eq;

@Repository
public class CookBookCacheRepository extends ServiceImpl<CookBookCacheMapper, CookBookCache> {

    @Autowired
    private CookBookCacheMapper cookBookCacheMapper;

    @Autowired
    private CookBookPreferenceRepository cookBookPreferenceRepository;

    // 可以在需要时调用 cookBookLikesMapper 的方法
    public List<Object> selectObjs(String colName, List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        QueryWrapper<CookBookCache> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        // 使用 DISTINCT 关键字去重，查询 dish_from 列
        queryWrapper.select("DISTINCT "+colName);
        // 执行查询，返回不重复的 dish_from 列表
        return cookBookCacheMapper.selectObjs(queryWrapper);
    }

    public List<CookBookCache> findDishInCache(Integer startComplex, Integer endComplex, String tasty, String dishFrom){
        QueryWrapper<CookBookCache> queryWrapper = new QueryWrapper<>();
        List<String> unlikeNames = getUnlikeNames();
        queryWrapper.lambda()
                .gt(startComplex!=null,CookBookCache::getComplex, startComplex)
                .le(endComplex!=null,CookBookCache::getComplex, endComplex)
                .notIn(CollectionUtils.isNotEmpty(unlikeNames),CookBookCache::getDishName, unlikeNames)
                .eq(CookBookCache::getTasty, tasty)
                .eq(CookBookCache::getDishFrom, dishFrom);
        return list(queryWrapper);
    }

    public List<String> getUnlikeNames(){
        List<String> unLikesId = Optional.ofNullable(cookBookPreferenceRepository.getUnLikesId()).orElse(new ArrayList<>());
        return selectObjs("dish_name", unLikesId).stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

}
