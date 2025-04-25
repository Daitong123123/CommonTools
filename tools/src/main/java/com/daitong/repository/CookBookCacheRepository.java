package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.CookBookCache;
import com.daitong.repository.mapper.CookBookCacheMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CookBookCacheRepository extends ServiceImpl<CookBookCacheMapper, CookBookCache> {

    public List<CookBookCache> findDishInCache(Integer startComplex, Integer endComplex, String tasty, String dishFrom){
        QueryWrapper<CookBookCache> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .gt(startComplex!=null,CookBookCache::getComplex, startComplex)
                .le(endComplex!=null,CookBookCache::getComplex, endComplex)
                .eq(CookBookCache::getTasty, tasty)
                .eq(CookBookCache::getDishFrom, dishFrom);
        return list(queryWrapper);
    }
}
